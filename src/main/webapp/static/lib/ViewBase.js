/**
 * @author liujintao
 */
define(['lodash', 'jquery', 'events', 'TplUtil', 'require'], function (_, $, events, TplUtil, require) {

    function ViewBase(opts) {
        this.pid = opts.pid || Date.now().valueOf();
        var _this = this;
        this.target = opts.target;
        this.model = opts.model;
        this.el = $(opts.el);
        this.el.attr("page-id", this.pid);
        this.model && this.model.on("update", function (data) {
            _this.update(data);
        });
        this.delegateEvents();
        this.init(opts);
    }

    _.extend(ViewBase.prototype, {
        el: null,
        events: {},
        init: function (opts) {
        },
        getTpl: function (tplFile, id) {
            return TplUtil.getTpl(tplFile, id);
        },
        update: function () {
        },
        render: function (target) {
            $(target).append(this.el);
        },
        getModel: function () {
            return this.model;
        },
        setModel: function (model) {
            if (this.model) {
                this.model.off("update");
                this.model.off("destroy");
            }
            this.model = model;
            this.model && this.model.on("update", $.proxy(this.update, this));
            this.model && this.model.on("destroy", $.proxy(this.destroy, this));
        },
        show: function () {
            $(this.el).show();
        },
        hide: function () {
            $(this.el).hide();
        },
        loadCss: function (cssPath) {

            cssPath = require.toUrl(cssPath);
            if ($('head').find("link[href='" + cssPath + "']").length > 0) {
                return;
            }
            var css = $("<link rel='stylesheet' type='text/css'>");
            css.attr("href", cssPath);
            $("head").append(css);
        },
        getEl: function () {
            return this.el;
        },
        destroy: function () {
            this.undelegateEvents();
            this.el.remove();
        },
        empty: function () {
            this.el.empty();
        },
        undelegateEvents: function () {
            var keys = Object.keys(this.events);
            var _this = this;
            for (var i = 0; i < keys.length; i++) {
                var evtName = keys[i].split("->")[0];
                var evtTarget = keys[i].split("->")[1];
                var handler = _this[_this.events[keys[i]]];
                _this.el.undelegate(evtTarget, evtName, $.proxy(handler, _this));
            }
        },
        delegateEvents: function () {
            var keys = Object.keys(this.events);
            var _this = this;
            
            for (var i = 0; i < keys.length; i++) {

                var evtName = keys[i].split("->")[0];
                var evtTarget = keys[i].split("->")[1];

                var handler = _this[_this.events[keys[i]]];

                _this.el.undelegate(evtTarget, evtName, $.proxy(handler, _this)).delegate(evtTarget, evtName, $.proxy(handler, _this));
            }
        }
    }, events);
    ViewBase.extend = function (protoProps, staticProps) {
        var parent = this;
        var child;
        if (protoProps && _.has(protoProps, 'constructor')) {
            child = protoProps.constructor;
        } else {
            child = function () {
                return parent.apply(this, arguments);
            };
        }

        // Add static properties to the constructor function, if supplied.
        _.extend(child, parent, staticProps);

        var Surrogate = function () {
            this.constructor = child;
        };
        Surrogate.prototype = parent.prototype;
        child.prototype = new Surrogate;
        if (protoProps)
            _.extend(child.prototype, protoProps);
        if (staticProps)
            _.extend(child.prototype, staticProps);
        child.__super__ = parent.prototype;

        return child;
    };
    return ViewBase;
});
