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
            <button class="demo-1-balance-submit">查询</button>
            <div class="demo-1-balance-resp">

            </div>
        </div>
    </div>

    <div class="demo-1-flowSet layout-flow">
        <h3>套餐查询</h3>
        <div>
            <label>查询号码:<input type="text" class="demo-1-flowset-mobile" name="mobile"/></label>
        </div>
        <div>
            <label>查询月份:<input type="month" class="demo-1-flowset-month" name="month"/></label>
        </div>
        <button class="demo-1-flowset-submit">查询</button>
        <div class="demo-1-flowset-resp">

        </div>
    </div>

    <div class="demo-1-chargeInfo layout-flow">
        <h3>交费历史查询</h3>
        <div>
            <label>查询号码:<input type="text" class="demo-1-chargeInfo-mobile" name="mobile"/></label>
        </div>
        <div>
            <label>查询月份:<input type="month" class="demo-1-chargeInfo-month" name="month"/></label>
        </div>
        <button class="demo-1-chargeInfo-submit">查询</button>
        <div class="demo-1-chargeInfo-resp">

        </div>
    </div>
</script>

<script type="text/template" id="demo-1-balance-resp-tpl">
    <div class="demo-1-balance-resp-ctn">
        <h5>账户余额: <%=data.balanceAmount%>分(<%= (+data.balanceAmount / 100).toFixed(2)%>元)</h5>
        <h5>欠费金额: <%=data.shouldCharge%>分(<%= (+data.shouldCharge / 100).toFixed(2)%>元)</h5>
        <h5>实时话费: <%=data.sumCharge%>分(<%= (+data.sumCharge / 100).toFixed(2)%>元)</h5>
    </div>
</script>

<script type="text/template" id="demo-1-flowset-resp-tpl">
    <div class="demo-1-flowset-resp-ctn">
        <%for(var i in data){
            var ppAcc = data[0];
        %>
        <h5>父套餐名称: <%=ppAcc.prodOffName%></h5>
        <h5>开始时间: <%=ppAcc.pStartTime%></h5>
        <h5>结束时间: <%=ppAcc.pEndTime%></h5>
            <%for(var j in ppAcc.subAccuInfoList){
                var subAcc = ppAcc.subAccuInfoList[j];
            %>
                <hr/>
                <ul>
                    <li>子套餐名称: <%=subAcc.accuName%></li>
                    <li>累计周期开始时间: <%=subAcc.startTime%></li>
                    <li>累计周期结束时间: <%=subAcc.endTime%></li>
                    <li>本月套餐总量: <%=subAcc.accuAmount%>KB
                        (<%=(+subAcc.accuAmount / 1024).toFixed(2)%>M)</li>
                    <li>本月套餐已使用量: <%=subAcc.usedAmount%>KB
                        (<%=(+subAcc.usedAmount / 1024).toFixed(2)%>M)</li>
                    <li>结转流量总量: <%= +subAcc.transferAccuAmount%>KB
                        (<%=(+subAcc.transferAccuAmount / 1024).toFixed(2)%>M)</li>
                    <li>结转流量使用量: <%= +subAcc.transferUsedAmount%>KB
                        (<%=(+subAcc.transferUsedAmount / 1024).toFixed(2)%>M)</li>
                </ul>
            <%}%>
        <%}%>
    </div>
</script>

<script type="text/template" id="demo-1-chargeInfo-resp-tpl">
    <div class="demo-1-chargeInfo-resp-ctn">
        <%if(data.length > 0){%>
            <%for(var i in data){
            var payment = data[0];
            %>
                <hr/>
                <h5>交费金额: <%=payment.paymentAmount%>分</h5>
                <h5>交费方式: <%=payment.paymentMethod%></h5>
                <h5>交费时间: <%=payment.payTime%></h5>
            <%}%>
        <%}else{%>
            <h5>无充值记录</h5>
        <%}%>
    </div>
</script>