<script type="text/template" id="demo-1-tpl">
    <div class="demo-1-balance layout-flow">
        <div class="demo-1-balance-ctn">
            <h3>余额查询</h3>
            <div>
                <label>查询号码:<input type="text" class="demo-1-balance-mobile" name="mobile"/></label>
            </div>
            <div>
                <label>查询类型:</label>
                <select name="queryType" class="demo-1-balance-type">
                    <option value="0">按照账户查</option>
                    <option value="1">按照用户查</option>
                </select>
            </div>
            <button class="demo-1-balance-subject">查询</button>
            <div class="demo-1-balance-resp">

            </div>
        </div>
    </div>
    <div class="demo-1-flowSet layout-flow"></div>
    <div class="demo-1-chargeInfo layout-flow"></div>
</script>

<!--##<h3>套餐查询</h3>-->
<!--##<form action="/flowSet">-->
<!--##    <label>查询号码:<input type="text" name="mobile"/></label>-->
<!--##    <label>查询类型:<input type="month" name="month"/></label>-->
<!--##    <input type="submit" value="提交"/>-->
<!--##</form>-->
<!--##<h3>交费查询</h3>-->
<!--##<form action="/chargeInfo">-->
<!--##    <label>查询号码:<input type="text" name="mobile"/></label>-->
<!--##    <label>查询类型:<input type="month" name="month"/></label>-->
<!--##    <input type="submit" value="提交"/>-->
<!--##</form>-->