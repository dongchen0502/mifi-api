/**
 * @author liujintao
 */
define(['lodash'],function(_){
    /*深度依赖underscore*/
    var eventSplitter = ",";
    /**
     * Events 是一个可以被mix到任意对象的模块，
     * 它拥有让对象绑定和触发自定义事件的能力。
     * 事件在被绑定之前是不需要事先声明的，
     *  var object = {};
     *    _.extend(object, Backbone.Events);
     *        object.bind("alert", function(msg) {
     *                alert("Triggered " + msg);
     *        });
     *   object.trigger("alert", "www.csser.com");
     * **/
    var Events  = {

        /**
         * 绑定 callback 函数到 object 对象。 当事件触发时执行回调函数 callback 。
         * 如果一个页面中有大量不同的事件，按照惯例使用冒号指定命名空间
         * ： "poll:start", 或 "change:selection"
         * */
        on:function (events, callback, context) {
            var calls, event, list;
            if (!callback) return this;

            events = events.split(eventSplitter);

            calls = this._callbacks || (this._callbacks = {});
            //保存回调函数,与事件 到对象的_callbacks上
            while (event = events.shift()) {
                //取出每个事件名放到_callbacks作用对象的属性，然后把回调函数push到这个数组中
                list = calls[event] || (calls[event] = []);
                list.push(callback, context);
            }

            return this;
        },

        /**
         * 删除一个或许多回调。如果“上下文”是null,删除所有的回调　　
         * 与功能。如果“回调”是null,删除所有的回调　
         * 事件。如果“事件”是null,移除所有绑定回所有事件
         *
         * object.unbind("change", onChange);  只移除onChange回调函数
         *  object.unbind("change");           移除所有 "change" 回调函数
         * object.unbind();                    移除对象的所有回调函数
         *
         * **/
        off:function (events, callback, context) {
            var event, calls, list, i;

            // No events, or removing *all* events.
            if (!(calls = this._callbacks)) return this;
            if (!(events || callback || context)) {
                delete this._callbacks;
                return this;
            }

            events = events ? events.split(eventSplitter) : _.keys(calls);

            // Loop through the callback list, splicing where appropriate.
            while (event = events.shift()) {
                if (!(list = calls[event]) || !(callback || context)) {
                    delete calls[event];
                    continue;
                }

                for (i = list.length - 2; i >= 0; i -= 2) {
                    if (!(callback && list[i] !== callback || context && list[i + 1] !== context)) {
                        list.splice(i, 2);
                    }
                }
            }

            return this;
        },

        /**
         * / /触发一个或许多事件,解雇所有绑定回调。回调是　　
         * / /通过相同的参数作为“触发”是,除了事件的名字　　
         * / /(除非你监听的“所有”,这将导致你的回调　　
         * / /接收真实的事件名称作为第一个参数)。
         * **/
        trigger:function (events) {
            var event, calls, list, i, length, args, all, rest;
            if (!(calls = this._callbacks)) return this;

            rest = [];
            //事件分解
            events = events.split(eventSplitter);

            // Fill up `rest` with the callback arguments.  Since we're only copying
            // the tail of `arguments`, a loop is much faster than Array#slice.
            for (i = 1, length = arguments.length; i < length; i++) {
                rest[i - 1] = arguments[i];
            }

            // For each event, walk through the list of callbacks twice, first to
            // trigger the event, then to trigger any `"all"` callbacks.
            while (event = events.shift()) {
                //复制回调列表来防止修改
                if (all = calls.all) all = all.slice();
                if (list = calls[event]) list = list.slice();

                // 执行事件回调
                if (list) {
                    for (i = 0, length = list.length; i < length; i += 2) {
                        list[i].apply(list[i + 1] || this, rest);
                    }
                }

                // 执行 "all" 回调.
                if (all) {
                    args = [event].concat(rest);
                    for (i = 0, length = all.length; i < length; i += 2) {
                        all[i].apply(all[i + 1] || this, args);
                    }
                }
            }

            return this;
        }
    };    
    return Events;
});
