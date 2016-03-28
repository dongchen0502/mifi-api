
require(['jquery'],function($){

    $(function(){
        require(['demoView'],function(demoView){
            new demoView({pid : 'page-demo', el : $('.api-content')}).render();
        });
    });
});
