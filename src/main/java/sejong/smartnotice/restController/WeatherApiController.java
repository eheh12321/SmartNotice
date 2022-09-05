package sejong.smartnotice.restController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import sejong.smartnotice.domain.Region;
import sejong.smartnotice.domain.Weather;
import sejong.smartnotice.dto.WeatherResponseDTO;

import javax.persistence.EntityManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
public class WeatherApiController {

    private final EntityManager em;

    @Value("${weatherApi.serviceKey}")
    private String serviceKey;

    @GetMapping
    @Transactional
    public ResponseEntity<WeatherResponseDTO> getRegionWeather(@RequestParam Long regionId) {

        Region region = em.find(Region.class, regionId);
        StringBuilder urlBuilder =  new StringBuilder("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtNcst");

        LocalDateTime now = LocalDateTime.now();
        String yyyyMMdd = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int hour = now.getHour();
        int min = now.getMinute();
        if(min <= 30) { // 해당 시각 발표 전에는 자료가 없음 - 이전시각을 기준으로 해야함
            hour -= 1;
        }
        String hourStr = hour + "00"; // 정시 기준
        String nx = Integer.toString(region.getNx());
        String ny = Integer.toString(region.getNy());
        String currentChangeTime = now.format(DateTimeFormatter.ofPattern("yy.MM.dd HH"));

        // 기준 시각이 이미 존재하고 있다면 API 요청 없이 기존 자료 그대로 넘김
        Weather prevWeather = region.getWeather();
        if(prevWeather != null && prevWeather.getLastUpdateTime() != null) {
            if(prevWeather.getLastUpdateTime().equals(currentChangeTime)) {
                log.info("기존 자료를 재사용합니다");
                WeatherResponseDTO dto = WeatherResponseDTO.builder()
                        .weather(prevWeather)
                        .message("OK").build();
                return ResponseEntity.ok(dto);
            }
        }

        log.info("연월일: {}", yyyyMMdd);
        log.info("시각: {}", hourStr);
        log.info("NX: {}, NY: {}", nx, ny);

        try {
            urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + serviceKey);
            urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
            urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("1000", "UTF-8")); /*한 페이지 결과 수*/
            urlBuilder.append("&" + URLEncoder.encode("dataType","UTF-8") + "=" + URLEncoder.encode("JSON", "UTF-8")); /*요청자료형식(XML/JSON) Default: XML*/
            urlBuilder.append("&" + URLEncoder.encode("base_date","UTF-8") + "=" + URLEncoder.encode(yyyyMMdd, "UTF-8")); /*‘21년 6월 28일 발표*/
            urlBuilder.append("&" + URLEncoder.encode("base_time","UTF-8") + "=" + URLEncoder.encode(hourStr, "UTF-8")); /*06시 발표(정시단위) */
            urlBuilder.append("&" + URLEncoder.encode("nx","UTF-8") + "=" + URLEncoder.encode(nx, "UTF-8")); /*예보지점의 X 좌표값*/
            urlBuilder.append("&" + URLEncoder.encode("ny","UTF-8") + "=" + URLEncoder.encode(ny, "UTF-8")); /*예보지점의 Y 좌표값*/

            URL url = new URL(urlBuilder.toString());
            log.info("request url: {}", url);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");

            BufferedReader rd;
            if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            rd.close();
            conn.disconnect();
            String data = sb.toString();

            Double temp = null;
            Double humid = null;
            Double rainAmount = null;

            log.info(data);

            JSONObject jObject = new JSONObject(data);

            JSONObject response = jObject.getJSONObject("response");
            JSONObject body = response.getJSONObject("body");
            JSONObject items = body.getJSONObject("items");
            JSONArray jArray = items.getJSONArray("item");
            for(int i = 0; i < jArray.length(); i++) {
                JSONObject obj = jArray.getJSONObject(i);

                String baseDate = obj.getString("baseDate");
                String baseTime = obj.getString("baseTime");
                String category = obj.getString("category");
                double obsrValue = obj.getDouble("obsrValue");

                switch (category) {
                    case "T1H":
                        temp = obsrValue;
                        break;
                    case "RN1":
                        rainAmount = obsrValue;
                        break;
                    case "REH":
                        humid = obsrValue;
                        break;
                }

                System.out.println("baseDate = " + baseDate);
                System.out.println("baseTime = " + baseTime);
                System.out.println("category = " + category);
                System.out.println("obsrValue = " + obsrValue);
            }

            Weather weather = new Weather(temp, rainAmount, humid, currentChangeTime);
            region.updateRegionWeather(weather);
            WeatherResponseDTO dto = WeatherResponseDTO.builder()
                    .weather(weather)
                    .message("OK").build();
            return ResponseEntity.ok(dto);

        } catch (IOException e) {
            WeatherResponseDTO dto = WeatherResponseDTO.builder()
                    .weather(null)
                    .message("날씨 정보를 불러오는 중 오류가 발생했습니다").build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(dto);
        }
    }
}
