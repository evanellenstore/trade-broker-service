<#-- Retrieve the number of steps; default to 1 if not provided -->
<#assign totalSteps = stepCount?default(1)>

<html>
  <body>
    <#list 1..totalSteps as i>
      <#-- Dynamically get the values for the current step -->
      <#assign dataList = .data_model["step" + i]>
      <#assign candleType = .data_model["step" + i + "CandleType"]>
      <#assign message = .data_model["step" + i + "Massege"]>
      
      <h2>${message}</h2>
      <#if dataList?has_content>
        <table border="1">
          <tr>
            <th>Stock Name</th>
            <th>changePercent</th>
            <th>changeOIPercent</th>
            <th>Nifity50 Candle Type</th>
          </tr>
          <#list dataList as stock>
            <tr>
              <td>${stock.stockName}</td>
              <td>${stock.changePercent} %</td>
              <td>${stock.changeOIPercent} %</td>
              <td>${candleType}</td>
            </tr>
          </#list>
        </table>
      <#else>
        <p>No stock changes available.</p>
      </#if>
      <br/>
    </#list>
  </body>
</html>
