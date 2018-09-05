app.service("uploadService",function ($http) {

    this.uploadFile = function () {
        var formData = new FormData();
        console.log(file.files[0]);
        formData.append("file", file.files[0]);
        return $http({
            url:"../upload.do",
            method:"post",
            data:formData,
            headers:{"Content-Type": undefined},
            transFormRequest: angular.identity
        });
    };
});