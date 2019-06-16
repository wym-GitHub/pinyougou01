 //控制层 
app.controller('itemCatController' ,function($scope,$controller   ,itemCatService,typeTemplateService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		itemCatService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		itemCatService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		itemCatService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){
        $scope.entity.parentId=$parentId;
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=itemCatService.update( $scope.entity ); //修改  
		}else{
			serviceObject=itemCatService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					$scope.findByParentId($parentId);

				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		itemCatService.dele( $scope.ids ).success(
			function(response){
				if(response.success){
					$scope.findByParentId($parentId);//刷新列表
					$scope.ids=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		itemCatService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}


	//根据上级id查询
	//定义一个变量记录上级id
	$scope.findByParentId=function (parentId) {
		$parentId=parentId;
        itemCatService.findByParentId(parentId).success(
        	function (response) {
				$scope.list=response;
            }
		)

    }

    //面包屑
    //1.1定义分类级别,控制表面包屑显示
    $scope.grade=1;//页面初始加载分类为级别一;

    //设置级别
    $scope.setGrade=function (value) {
        $scope.grade=value;//每次点击查询下级,级别加一
    }

    //根据级别确定显示的分类名称
    $scope.selectList=function (entity) {

        if($scope.grade==1){
            //当级别为1时,面包屑只显示,一级目录的名称
            $scope.entity_2={};
            $scope.entity_3={};
        }
        if($scope.grade==2){
            //当级别为2时,面包屑显示,一级目录的名称和二级目录的名称
            $scope.entity_2=entity;
            $scope.entity_3={};
        }
        if($scope.grade==3){
            //当级别为3时,面包屑显示三级目录

            $scope.entity_3=entity;
        }
      //查询下级目录
        $scope.findByParentId(entity.id);

    }


    //模板下拉列表
    $scope.typeList={data:[]}
    $scope.typeOptins=function () {

        typeTemplateService.typeOptions().success(
        	function (response) {
				$scope.typeList={data:response};
            }
		)
    }

});	
