// 파일 저장 관련 자바스크립트 함수 모음

// 파일 용량 및 확장자 검사
function checkExtension(fileName, fileSize) {
    var maxSize = 10 * 1024 * 1024; // 10MB
    var regex = new RegExp("(.*?)\.(mp3)$");

    if (fileSize > maxSize) {
        alert("파일 사이즈 초과");
        return false;
    }
    if (!regex.test(fileName)) {
        alert("mp3 확장자만 업로드 할 수 있습니다.");
        return false;
    }
    return true;
}

// uuid 이름 생성
function uuidV4() {
    return ([1e7] + -1e3 + -4e3 + -8e3 + -1e11).replace(/[018]/g, c =>
        (c ^ crypto.getRandomValues(new Uint8Array(1))[0] & 15 >> c / 4).toString(16)
    );
}