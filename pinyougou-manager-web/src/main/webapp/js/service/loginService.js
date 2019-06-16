app.service('loginService',function ($http) {

    //获取登录名
    this.getLoginName=function () {
        return $http.get('../login/getLoginName.do');
    }

})