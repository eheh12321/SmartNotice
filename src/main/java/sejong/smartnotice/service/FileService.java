package sejong.smartnotice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@Service
public class FileService {

    @Value("${resources.location}")
    private String resourceLocation;

    /**
     * resourceLocation -> /home/~~/storage
     * directory -> /2022/10/24/
     * fileName -> fileName
     */
    public boolean saveFile(byte[] audioContents, String fileName, String directory) {
        String path = resourceLocation + directory + fileName + ".mp3";
        log.info(">> 파일 저장 경로: {}", path);
        try (OutputStream out = new FileOutputStream(path)) {
            out.write(audioContents);
            log.info("파일 저장에 성공했습니다");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            log.warn("파일 저장에 실패하였습니다");
            return false;
        }
    }

    /**
     * 날짜에 따른 업로트 파일 경로 생성 (연-월-일)
     * - 디텍토리가 존재하지 않는다면 새로 생성함
     * - ex) /2022/10/15/
     */
    public String getDirectory() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String datePath =
                File.separator
                        + sdf.format(date).replace("-", File.separator)
                        + File.separator;

        String fullPath = resourceLocation + datePath;
        File file = new File(fullPath);
        if(!file.exists()) {
            log.info(">> 새 디렉토리를 생성했습니다: {}", Paths.get(fullPath).toUri());
            file.mkdirs(); // 디렉토리가 존재하지 않는다면 새로 생성
        }
        return datePath;
    }
}
