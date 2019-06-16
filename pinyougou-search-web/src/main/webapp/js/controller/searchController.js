app.controller("searchController", function ($scope,$location, searchService) {

    //初始化搜索对象
    $scope.searchMap = {
        'keywords': '',
        'category': '',
        'brand': '',
        'spec': {},
        price: "",
        pageNo: 1,
        pageSize: 40,
        sort: '',
        sortField: ''
    };

    $scope.search = function () {
        $scope.searchMap.pageNo = parseInt($scope.searchMap.pageNo);

        searchService.search($scope.searchMap).success(
            function (response) {
                $scope.resultMap = response;
                buildPageLabel();//查询返回总页数,和总记录数,然后调用方法,显示分页标签;
            }
        )
    }
    //添加搜索项
    $scope.addSearchItem = function (key, value) {
        if (key == 'category' || key == 'brand' || key == 'price') {
            $scope.searchMap[key] = value;

        } else {
            $scope.searchMap.spec[key] = value;
        }
        $scope.search();//每次添加,搜索项,就会执行查询

    }
    //移除搜索项
    $scope.removeSearchItem = function (key) {
        if (key == 'category' || key == 'brand' || key == 'price') {
            $scope.searchMap[key] = "";

        } else {
            //对象移除属性,用delete
            delete $scope.searchMap.spec[key];
        }
        $scope.search();//每次移除搜索项,就会执行查询
    }

    //构建分页标签
    buildPageLabel = function () {
        $scope.pageLabel = [];//构建分页栏属性
        var maxPageNo = $scope.resultMap.totalPages;//获取最后的页码
        var firstPage = 1;//开始页码
        var lastPage = maxPageNo//截止页码
        $scope.firstDot = true;
        $scope.lastDot = true;
        if ($scope.resultMap.totalPages >= 5) {//大于五页,显示部分页码
            if (($scope.searchMap.pageNo <= 3)) {

                //当前页小于三,则显示前五页,右边的点显示
                lastPage = 5;
                $scope.firstDot = false;

            } else if ($scope.searchMap.pageNo >= lastPage - 2) {
                //当当前页,大于总页数减二,则显示后五五页,左边的点要显示
                firstPage = maxPageNo - 4;
                $scope.lastDot = false;

            } else {

                firstPage = $scope.searchMap.pageNo - 2;
                lastPage = $scope.searchMap.pageNo + 2;

            }
        } else {
            //当查询结果小于五页,两边的点都不显示
            $scope.firstDot = false;
            $scope.lastDot = false;

        }

        //循环产生页码标签,把页数加到pageLabel集合中
        for (var i = firstPage; i <= lastPage; i++) {
            $scope.pageLabel.push(i);

        }

    }
    //提交页码查询
    $scope.queryByPage = function (pageNo) {

        //页码验证,如果输入的页码小于0或者,大于总页数,直接return
        if (pageNo < 1 || pageNo > $scope.resultMap.totalPages) {
            return;
        }

        $scope.searchMap.pageNo = pageNo;
        $scope.search();

    }

    //是否为第一页,上一页是否可用
    $scope.isTopPage = function () {
        if ($scope.searchMap.pageNo == 1) {
            return true;
        } else {
            return false;
        }
    }

    $scope.isEndPage = function () {
        if ($scope.searchMap.pageNo == $scope.resultMap.totalPages) {
            return true;
        } else {
            return false;
        }
    }
    //排序
    $scope.sortSearch = function (sort, sortField) {
        $scope.searchMap.sort = sort;
        $scope.searchMap.sortField = sortField;
        $scope.search();

    }

    //隐藏品牌列表
    $scope.keywordsIsBrand = function () {
        for (var i = 0; i < $scope.resultMap.brandList.length; i++) {
            if ($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text)>=0) {
                return true;
            }
        }

        return false;
    }

    //接受参数,页面参数,并查询
    $scope.loadkeywords=function () {
       $scope.searchMap.keywords= $location.search()['keywords'];
       if($scope.searchMap.keywords!=''){
           $scope.search();
       }

    }

})