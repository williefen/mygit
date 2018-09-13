app.controller("searchController", function ($scope,$location, searchService) {

    // 搜索对象
    $scope.searchMap={"keywords":"","category":"","brand":"","spec":{},"price":"","pageNo":1,"pageNo":20,"addSortField":"","sort":""};
    
    
    $scope.search = function () {
        searchService.search($scope.searchMap).success(function (response) {
            $scope.resultMap = response;
            //构建页面分页导航条信息
            buildPageInfo();
        });

    };
    //添加过滤查询条件
    $scope.addSearchItem=function (key, value) {
        if("category" == key || key == "brand" || "price" == key){
            $scope.searchMap[key]=value;

        }else{

            //规格
            $scope.searchMap.spec[key]=value;
        }
        $scope.searchMap.pageNo=1;
        // 点击过滤条件后需要重新搜索
        $scope.search();
    };

       // 移除过滤条件
    $scope.removeSearchItem = function (key) {
         if("category" == key || key == "brand" || "price" == key){
             $scope.searchMap[key] = "";
         }else{
           // 规格
            delete  $scope.searchMap.spec[key];
         }
        $scope.searchMap.pageNo=1;
        $scope.search();
    };
     // 构建分页导航条
    buildPageInfo=function () {
        // 分页导航条显示页号集合
       $scope.pageNoList= [];
       // 在导航条显示总页号数目
       var showPageCount=5;
         // 起始页号
       var startPageNo=1;
         // 结束页号
       var endPageNo=$scope.resultMap.totalPages;

       if ($scope.resultMap.totalPages>showPageCount){
          // 当前页所需间隔页数
          var interval=Math.floor((showPageCount/2));

          startPageNo=parseInt($scope.searchMap.pageNo) -interval;
          endPageNo=parseInt($scope.searchMap.pageNo) + interval;
          // 处理页号越界
          if (startPageNo>0) {

              if (endPageNo>$scope.resultMap.totalPages) {
                  startPageNo = startPageNo - (endPageNo - $scope.resultMap.totalPages);
                  endPageNo = $scope.resultMap.totalPages;
                 }
              }else {
              // 起始页号小于1
              // endPageNo = endPageNo - (startPageNo -1);
                 endPageNo=showPageCount;
                 startPageNo=1;
              }
          }
           // 导航条中前三个点
         $scope.frontDot=false;
         if(startPageNo>1){
           $scope.frontDot = true;

       }
            //导航条中的后面3个点
       $scope.backDot=false;
       if(endPageNo<$scope.resultMap.totalPages){
           $scope.backDot = true;

       }
        // 设置要显示的页号
       for (var i=startPageNo;i <= endPageNo;i++) {
           $scope.pageNoList.push(i);
       }
   };

   // 判断是否为当前页
     $scope.isCurrentPage=function (pageNo) {
         return parseInt($scope.searchMap.pageNo) == pageNo;
     };

    //根据页号查询
       $scope.queryByPageNo =function (pageNo) {
           if (0<pageNo && pageNo<= $scope.resultMap.totalPages){
            $scope.searchMap.pageNo = pageNo;
            $scope.search();
           }
       };
       // 添加排序
    $scope.addSortField=function (sortField,sort) {
        $scope.searchMap.sortField = sortField;
        $scope.searchMap.sort = sort;

        $scope.search();
        
    };

    $scope.loadKeywords=function () {

        $scope.searchMap.keywords=$location.search()["keywords"];
        //获取地址栏中的搜索关键字
        $scope.search();
    };


});