// 입력 폼 관련 편의 기능 자바스크립트 함수 모음

// 입력 폼 필드 에러 검증 시 에러문구 출력 함수
function markingErrorField(response) {
    const responseJson = JSON.parse(response.responseText);
    const errorFields = responseJson.errors;
    if(!errorFields) {
        alert(response.responseText);
        return;
    }

    var error;
    for(var i = 0, length = errorFields.length; i < length; i++) {
        error = errorFields[i];
        $('#' + error.field + '-field-error').text(error.message);
    }
}

// 전화번호 입력 폼 자동 하이픈 삽입기
function autoHyphen(target) {
    target.value = target.value
        .replace(/[^0-9]/g, '')
        .replace(/^(\d{0,3})(\d{0,4})(\d{0,4})$/g, "$1-$2-$3").replace(/(\-{1,2})$/g, "");
}