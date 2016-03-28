/**
 * Created by liunickluck on 15/2/25.
 */
define([
    'lodash',
    'jquery',
    'require'
], function(
    _,
    jQuery,
    require
){

    (function($,context){
        var tpls = {};
        var paperData = null;

        function getTpl(url,id){


            if(tpls[id]){
                return tpls[id];
            }else{
                url = require.toUrl(url);
                $.ajax({
                    url:url,
                    type:'get',
                    dataType:"text",
                    async:false
                    //crossDomain:true
                }).done(function(text){
                    if(!text)return;
                    var $module = $('<div/>');
                    $module.html(text);
                    var $tmp = $module.find('script');
                    for(var i=0;i<$tmp.length;i++){
                        tpls[$tmp[i].id] = $tmp[i].innerHTML;
                    }
                    $module.remove();
                });

                return tpls[id];
            }

        }

        /**
         *
         * @param elemStr container
         * @param res resource array
         * @param defaultId default option id [option]
         * @param idKey id key [option]
         * @param valueKey value key [option]
         */
        function renderSelect2(elemStr, res, option) {

            var defaultOption = {
                dataAttributes: [],
                defaultId: null,
                idKey: 'id',
                valueKey: 'name',
                extraData: null,
                chooseFirstItem: true
            };

            var p = $.extend(defaultOption, option);


            var array = _.map(res, function(item){

                var d = {
                    id: item[p.idKey],
                    text: item[p.valueKey]
                };

                if (p.dataAttributes) {
                    _.each(p.dataAttributes, function (e) {
                        d[e.id] = item[e.value];
                    });
                }

                return d;
            });

            var array = _.sortBy(array, p.sort);

            var elem = $(elemStr);
            if (elem.hasClass('data-select2')) {
                elem.html('');
            }
            elem.select2({
                data: array,
                theme: 'bootstrap',
                language: 'ch'
            });

            if (!elem.hasClass('data-select2')) {
                elem.addClass('data-select2');
            }

            if (!res || res.length == 0) return;

            if(p.chooseFirstItem){
                var defaultId = p.defaultId ? p.defaultId : array[0]["id"];
                elem.val(defaultId).trigger('change', p.extraData);
            }
        }


        function initDatePickerWithSeconds(datePickerElem, model, before, after) {

            var dateFormatter = 'YYYY-MM-DD hh:mm';
            before = before > 0 ? before : 35;
            after = after > 0 ? after : 10;
            var now = moment().add(after, 'days').format(dateFormatter);
            var start = moment().subtract(before, 'days').format(dateFormatter);


            datePickerElem.daterangepicker({
                language: 'cn',
                format :dateFormatter,
                locale: calendar.zh,
                timePicker: true,
                timePickerIncrement: 10,
                startDate: start,
                endDate: now
            },function(start, end, label) {
                model.set("startDate", start.format(dateFormatter));
                model.set("endDate", end.format(dateFormatter));
                model.trigger("change:dateSpan", model.data);
            });

            if ($.trim(datePickerElem.val()) == '') {


                datePickerElem.val(start + ' - ' + now);
                model.set("startDate", start);
                model.set("endDate", now);
                model.trigger("change:dateSpan", model.data);
            }

        }

        function keys(obj) {
            return _.map(obj, function(value, key) {return key});
        }

        function getPaperData(){
            return this.paperData;
        }

        context.TplUtil = {
            getTpl:getTpl,
            renderSelect2: renderSelect2,
            initDatePickerWithSeconds:initDatePickerWithSeconds,
            keys: keys,
            getPaperData: getPaperData
        }
    })(jQuery,window);

    return window.TplUtil;
});

