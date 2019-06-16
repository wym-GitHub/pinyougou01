app.controller('baseController',function ($scope) {

    //列表刷新
    $scope.reloadList = function () {

        //条件分页查询替代了原始的分页查询
        $scope.search($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
    }

    //分页条
    $scope.paginationConf = {
        currentPage: 1,
        totalItems: 10,
        itemsPerPage: 10,
        perPageOptions: [10, 20, 30, 40, 50],
        onChange: function () {
            $scope.reloadList();
        }

    }

    //删除操作,步骤:1:定义一个数组,往里面添加数据
    $scope.ids = [];

    //判断复选框是否是选中状态,true则添加到数组中
    $scope.selectcheck = function ($event, id) {
        if ($event.target.checked) {
            $scope.ids.push(id);
        } else {
            //复选框变回false则查到索引从数组中删除
            var index = $scope.ids.indexOf(id);
            $scope.ids.splice(index, 1);//index代表删除的下标,1表示删除的个数

        }


    }

    //全选
    $scope.selectAll=function ($event) {
        $scope.ids = [];
        var isChecked=$event.target.checked;
        if(isChecked){
            for(var i=0;i<$scope.list.length;i++){
                $scope.ids.push($scope.list[i].id);
            }
        }
    }

    //jason转换
    $scope.jasonToString=function (jsonString,key) {
        var value="";
        var jason=JSON.parse(jsonString);
        for(var i=0;i<jason.length;i++){
            if(i>0){
                value+=","
            }
            value+=jason[i][key];
        }
        return value;
    }

    //从集合中按照key查询对象
    $scope.searchObjectByKey=function (list,key,keyvalue) {

        for(var i=0;i<list.length;i++){
           if(list[i][key]==keyvalue){
               return list[i];
           }
        }
        return null;
    }

})