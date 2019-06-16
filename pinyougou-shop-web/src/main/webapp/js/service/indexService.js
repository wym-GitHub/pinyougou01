app.service('indexService',function ($http) {

    this.findLoginName=function () {
        return $http.get('../index/findLoginName.do');
    }
})