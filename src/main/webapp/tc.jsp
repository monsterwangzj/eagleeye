<%@ page contentType="text/html;charset=GBK"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<title>FusionCharts Free Documentation</title>
<link rel="stylesheet" href="/graphics/style.css" type="text/css" />
<script language="JavaScript" src="/graphics/FusionCharts.js"></script>
</head>

<body>
<table width="98%" border="0" cellspacing="0" cellpadding="3" align="center">
  <tr> 
    <td valign="top" class="text" align="center"> <div id="chartdiv" align="center"> 
        FusionCharts. </div>
      <script type="text/javascript">
		   var chart = new FusionCharts("/graphics/FCF_Column3D.swf", "ChartId", "600", "350");
		   chart.setDataURL("/graphics/Column3D.xml");		   
		   chart.render("chartdiv");
		</script> </td>
  </tr>
  
  <!-- 
  <tr>
    <td valign="top" class="text" align="center">&nbsp;</td>
  </tr>
  <tr> 
    <td valign="top" class="text" align="center"><a href="/graphics/Column3D.xml" target="_blank"><img src="/graphics/BtnViewXML.gif" alt="View XML for the above chart" width="75" height="25" border="0" /></a></td>
  </tr> -->
</table>
</body>
</html>
