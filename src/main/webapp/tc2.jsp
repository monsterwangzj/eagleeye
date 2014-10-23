<%@ page contentType="text/html;charset=GBK"%>
<HTML>
     <HEAD>
         <TITLE>FusionCharts Free - Simple Column 3D Chart</TITLE>
         <style type="text/css">
         <!--
        body {
         font-family: Arial, Helvetica, sans-serif;
         font-size: 12px;
         }
         -->
         </style>
     </HEAD>
     <%
     		 String strXML="";
     		 
             strXML += "<graph caption='Monthly Unit Sales' xAxisName='Month' yAxisName='Units' decimalPrecision='0' formatNumberScale='0'>";
             strXML += "<set name='00' value='46' color='AFD8F8'/>";
             strXML += "<set name='01' value='85' color='F6BD0F'/>";
             strXML += "<set name='02' value='61' color='8BBA00'/>";
             strXML += "<set name='03' value='44' color='FF8E46'/>";
             strXML += "<set name='04' value='71' color='008E8E'/>";
             strXML += "<set name='05' value='90' color='D64646'/>";
             strXML += "<set name='06' value='69' color='8E468E'/>";
             strXML += "<set name='07' value='62' color='588526'/>";
             strXML += "<set name='08' value='36' color='B3AA00'/>";
             strXML += "<set name='09' value='44' color='008ED6'/>";
             strXML += "<set name='10' value='71' color='9D080D'/>";
             strXML += "<set name='11' value='90' color='A186BE'/>";
             strXML += "<set name='12' value='96' color='A186BE'/>";
             strXML += "<set name='13' value='96' color='A186BE'/>";
             strXML += "<set name='14' value='96' color='A186BE'/>";
             strXML += "<set name='15' value='96' color='A186BE'/>";
             strXML += "<set name='16' value='96' color='A186BE'/>";
             
             strXML += "</graph>";
      %>
     <BODY>
         <CENTER>
            <jsp:include page="/graphics/FusionChartsHTMLRenderer.jsp" flush="true"> 
                 <jsp:param name="chartSWF" value="/graphics/FCF_Column3D.swf" /> 
                 <jsp:param name="strURL" value="" /> <%-- /graphics/Line2D.xml --%>
                 <jsp:param name="strXML" value="<%=strXML%>" /> 
                 <jsp:param name="chartId" value="myFirst" /> 
                 <jsp:param name="chartWidth" value="600" /> 
                 <jsp:param name="chartHeight" value="300" />
                <jsp:param name="debugMode" value="false" />
             </jsp:include>
         </CENTER>
     </BODY>
 </HTML>