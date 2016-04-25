
require(['jquery'],function($){

    $(function(){
        require(['queryView'],function(queryView){
            new queryView({pid : 'page-demo', el : $('.api-content')}).render();
        });
    });
});
