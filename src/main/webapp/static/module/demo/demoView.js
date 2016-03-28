/**
 * Created by dongchen on 2016-03-29
 */
define(['ViewBase', 'jquery', 'lodash', 'ajaxUtil'], function (ViewBase, $, _, ajaxUtil) {
    var
        tplPath = './module/demo/demo.tpl';

    var view = ViewBase.extend({
        events : {
            'click -> .demo-1-balance-subject' : 'onClickBalanceQueryHandler',
            'click -> body' : 'onClickBody'
        },

        init: function (opts) {
            view.__super__.init(opts);
            this.loadCss('./module/demo/demo.css');
        },

        update: function () {
        },

        render: function () {
            this.el.html(_.template(this.getTpl(tplPath, "demo-1-tpl")));
        },

        onClickBalanceQueryHandler : function(event){
            
            var
                $target = $(event.target),
                mobile = $target.parent().find('.demo-1-balance-mobile').val(),
                type = $target.parent().find('.demo-1-balance-type').val();

            ajaxUtil.doRequest_v2('/balance', {mobile : mobile, queryType : type}).done(function(res){
                debugger;
            });
        },

        onClickBody : function(){
            alert('OK');
        }
    });
    return view;
});