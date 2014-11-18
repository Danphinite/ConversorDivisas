<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="moneda.Moneda, java.util.ArrayList, java.util.HashMap"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<style>
		@import 'estilos.css';
	</style>
</head>
<body>
	<table id="">
		<thead>
			<tr>
				<th>Unidad Origen</th>
				<th>Valor</th>
				<th>Unidad Destino</th>
				<th>Resultado</th>
				<th>Tasa de Intercambio</th>
			</tr>	
		</thead>
		<%
			ArrayList<Moneda> registro = (ArrayList<Moneda>) session.getAttribute("registro");
		HashMap<String, ArrayList<String>> monedas = (HashMap<String, ArrayList<String>>) application.getAttribute("monedas");
			if (registro!=null)
				for (Moneda moneda : registro) {
					out.println("<tr>\r\t\t\t<td title=\"" + monedas.get(moneda.getUnidad1()).get(0)+"\">"+ moneda.getUnidad1() + "</td>");
					out.print("\r\t\t\t<td>"+ moneda.getValorEntrada() + "</td>");
					out.print("\r\t\t\t<td title=\"" + monedas.get(moneda.getUnidad2()).get(0)+"\">"+ moneda.getUnidad2() + "</td>");
					out.print("\r\t\t\t<td>"+ moneda.getValorSalida() + "</td>");
					out.print("\r\t\t\t<td>"+ moneda.getFactorConversion()+ "</td>");
					out.print("\r\t\t</tr>");
				}
		%>
	</table>
	<br>
	<a href="" onclick="window.close()">Cerrar</a>
</body>
</html>