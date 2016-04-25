require.config({
    paths: {
        'jquery': 'lib/jquery/jquery.min',
        'TplUtil': 'lib/TplUtil',
        'lodash': 'lib/lodash',
        'events': 'lib/events',
        'ModelBase': 'lib/ModelBase',
        'ViewBase': 'lib/ViewBase',
        'bootbox': 'lib/bootbox.min',
        'bootstrap': 'lib/bootstrap.min',
        "moment": "lib/moment.min",
        'ajaxUtil': 'lib/ajaxUtil',
        'fileUpload': 'lib/ajaxfileupload',

        'queryView': 'module/demo/demoView'
        // 'groupView':'module/groupView',
        // 'indexView':'module/indexView',
        // 'singleView':'module/singleView',
        // 'Router':'bower_components/director/build/director.min',
        // 'dictionary':'EnumConstant/dictionary',
        // 'simple_statistics':'lib/simple_statistics.min',
        // 'bootstrap-table-zh-CN':'lib/bootstrap-table-zh-CN'
    },
    baseUrl: "/static",
    urlArgs: "v=" + "2016-04-25 11:49:54",
    // urlArgs: "v=" + new Date(),

    //这个配置是你在引入依赖的时候的包名
    shim: {
        // 'bootstrap': ['jquery'],
        'fileUpload': ['jquery']
    }
});
