app.service('brandService',function ($http) {
    this.findAll=function () {
        return  $http.get('http://localhost:9101/brand/findall.do');
    }
    //分页
    this.findPage=function (pageNum,pageSize) {
        return  $http.get('http://localhost:9101/brand/page.do?pageNum=' + pageNum + "&pageSize=" + pageSize);

    }
    //新增
    this.insert=function(entity){
        return $http.post("http://localhost:9101/brand/insert.do",entity);
    }
    //修改
    this.update=function (entity) {
        return $http.post("http://localhost:9101/brand/update.do",entity);

    }
    //单个查询
    this.findOne=function (id) {
        return $http.get("http://localhost:9101/brand/findById.do?id=" + id);
    }
    //删除
    this.delete=function (ids) {
        return  $http.get("../brand/delete.do?ids=" +ids);
    }
    //条件查询
    this.search=function (pageNum,pageSize,conditions) {
        return $http.post("../brand/search.do?pageNum=" + pageNum + "&pageSize=" + pageSize,conditions);
    }
    this.selectBrandList=function () {
        return $http.get("../brand/selectBrandList.do");
    }
})