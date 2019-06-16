app.controller('brandController', function ($scope,brandService,$controller) {

    $controller('baseController',{$scope:$scope});//继承

    //查找全部
    $scope.findAll = function () {

        brandService.findAll().success(
            function (response) {
                $scope.list = response;

            }
        )
    }

    //分页查询
    $scope.findPage = function (pageNum, pageSize) {

        brandService.findPage(pageNum, pageSize).success(
            function (response) {
                $scope.rowList = response.rows;
                $scope.paginationConf.totalItems = response.total;
            }
        )
    }




    //新增,修改
    $scope.save = function () {
        //修改和新增公用一个编辑框,点击保存的时候,需要进行判断,是修改操作还是新增操作
        var object;
        if ($scope.entity.id != null) {
            object = brandService.update();

        }else{
            object = brandService.insert();

        }

        object.success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();
                }
                else {
                    alert(response.message);
                }
            }
        )

    }
    //修改操作一.查询当前品牌信息

    $scope.findById = function (id) {

        $http.get("http://localhost:9101/brand/findById.do?id=" + id).success(
            function (response) {
                $scope.entity = response;
            }
        )
    }


    //删除操作步骤2:把id数组传给后台进行删除
    $scope.delete = function () {
        if(confirm("是否要删除")){
            brandService.delete($scope.ids).success(
                function (response) {
                    if (response.success) {
                        $scope.reloadList();
                        $scope.ids=[];//重置数组
                    }
                    else {
                        alert(response.message);
                    }
                }
            )
        }

    }

    //条件查询,取代原始分页查询
    $scope.conditions = {};
    $scope.search = function (pageNum, pageSize) {

        brandService.search(pageNum,pageSize,$scope.conditions).success(
            function (response) {


                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;


            }
        )
    }



})
