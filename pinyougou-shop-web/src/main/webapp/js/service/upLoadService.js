app.service("uploadService",function ($http) {

    this.uploadFile=function () {

        var formData=new FormData();
        formData.append("multipartFile",file.files[0]);//multipartFile和后台controller接受数据的参数名
        return $http({
            method:'post',
            url:"../upload.do",
            data:formData,

            headers:{'content-type':undefined},//post,get请求默认的content-type是application/jason,设置为undefined,浏览器会自动把contenttype设置为multipart/form-data
            transformRequest:angular.identity//序列化 formData

        })

    }


})