/**
 * @author liujintao
 */
define(['underscore', 'events'], function (_, event) {
    var Model = function (opts) {
        this.cid = _.uniqueId("c_");
        this.id = (opts && opts.id) || Date.now().valueOf();
        this.previousAttribute = {};
        var defaults;
        this.data = opts || {};
        this.changed = {};

        // 存在defaults默认信息，有的话就给attrs设置上
        if (defaults = _.result(this, 'defaults')) {
            this.data = _.defaults({}, this.data, defaults);
        }
        this.init(opts);
    };
    _.extend(Model.prototype, {
        init: function (opts) {

        },
        set: function (data, options) {
            var _this = this;
            if (data == null) {
                return this;
            }
            if (!this.data) {
                this.data = {};
            }
            if (this.validate(data)) {
                this.previousAttribute = _.clone(this.data);
                this.changed = {};
                if (arguments.length >= 2 && (typeof arguments[0] == 'string')) {
                    this.data[arguments[0]] = arguments[1];
                    if (!_.isEqual(this.data[arguments[0]], this.previousAttribute[arguments[0]])) {
                        this.changed[arguments[0]] = arguments[1];
                        if (!(options && options.silent)) {
                            this.trigger("change:" + arguments[0]);
                        }
                    }
                } else if (typeof data == "object") {
                    this.data = _.extend(this.data, data);
                    var keys = _.keys(data);
                    keys.forEach(function (key) {
                        if (!_.isEqual(data[key], _this.previousAttribute[key])) {
                            _this.changed[key] = data[key];
                            if (!(options && options.silent)) {
                                _this.trigger("change:" + key);
                            }
                        }
                    });
                } else {
                    throw new Error("model set方法调用参数错误");
                }
                if (!(options && options.silent)) {
                    this.trigger("update", {type: "update", id: this.id, data: this.data});
                }
            }
        },
        toJSON: function () {
            return _.clone(this.data || {});
        },
        validate: function (data) {
            return true;
        },
        hasChanged: function (attr) {
            if (attr == null) return !_.isEmpty(this.changed);
            return _.has(this.changed, attr);
        },
        getPreviousAttr: function (key) {
            if (key)
                return this.previousAttribute[key];
            else
                return this.previousAttribute;
        },
        reset: function () {
            this.data = {};
        },
        getCid: function () {
            return this.cid;
        },
        get: function (attr) {
            if (!this.data) {
                return null;
            }
            if (typeof attr == "string") {
                return _.clone(this.data[attr]);
            } else if (typeof attr == "object") {
                return _.clone(this.data);
            }
        },
        destroy: function () {
            this.previousAttribute = {};
            this.trigger("destroy", this.id);
            this.off("update");
        }
    }, event);

    Model.extend = function (protoProps, staticProps) {
        var parent = this;
        var child;
        if (protoProps && _.has(protoProps, 'constructor')) {
            child = protoProps.constructor;
        } else {
            child = function () {
                return parent.apply(this, arguments);
            };

        }
        _.extend(child, parent, staticProps);
        var Surrogate = function () {
            this.constructor = child;
        };
        Surrogate.prototype = parent.prototype;
        child.prototype = new Surrogate;
        if (protoProps)
            _.extend(child.prototype, protoProps);
        if (staticProps) {
            _.extend(child.prototype, staticProps);
        }
        child.__super__ = parent.prototype;

        return child;
    };
    return Model;
});
