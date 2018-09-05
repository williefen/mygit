app.service("brandService",function ($http) {

    this.findAll=function () {
        return  $http.get("../brand/findAll.do");
    };

    this.findPage=function(page,rows){
        return $http.get("../brand/findPage.do?page=" + page + "&rows=" + rows);
    };
    // 新增数据
    this.add=function (entity) {

        return  $http.post("../brand/add.do",entity);
    };
    // 更新数据
    this.update=function (entity) {

        return $http.post("../brand/update.do",entity);
    };

    // 根据主键id查询
    this.findOne=function (id){
        return $http.get("../brand/findOne.do?id="+id);
    };

    // 批量删除
    this.delete=function (selectedIds) {
        return $http.get("../brand/delete.do?ids="+selectedIds);
    };
      // 根据条件分页查询数据
    this.search=function(page,rows,searchEntity){

        return $http.post("../brand/search.do?page="+page+"&rows="+rows, searchEntity);
    };

    //查询格式化的品牌数据

    this.selectOptionList=function () {
        return $http.get("../brand/selectOptionList.do");
    };

});
