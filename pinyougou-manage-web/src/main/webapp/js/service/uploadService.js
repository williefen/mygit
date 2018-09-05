app.service("uploadService",function ($http) {

    this.uploadFile = function () {
        //创建表单数据 只用于html5
        var formData = new FormData();
        formData.append("file", file.files[0]);
        return $http({
            url:"../upload.do",
            method:"post",
            data:formData,
            headers:{"Content-Type": undefined},
            transformRequest: angular.identity
        });
    };
});