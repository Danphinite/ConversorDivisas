<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="moneda.Moneda, java.util.HashMap, java.util.ArrayList"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>Conversor de moneda</title>
	<style>
		@import 'estilos.css';
	</style>
</head>
<body>
	<%
		/* Recoge el mapa con las monedas */
		HashMap<String, ArrayList<String>> monedas = (HashMap<String, ArrayList<String>>) application.getAttribute("monedas");
		ArrayList<String> valores = (ArrayList<String>) application.getAttribute("valores");
		
		String unidad1 = request.getParameter("unidad1");
		if (unidad1 == null || request.getParameter("accion").equals("Reiniciar"))
			// se abre la p�gina por primera vez o se ha reiniciado
			unidad1 = "EUR"; // C�digo por defecto
		String unidad2 = request.getParameter("unidad2");
		if (unidad2 == null || request.getParameter("accion").equals("Reiniciar"))
			unidad2 = "USD";	
	%>
	<%!
		public String escribeLista(HashMap<String, ArrayList<String>> monedas,
															ArrayList<String> valores, String unidad) {
			String html="";
			String[][] regiones = {{"Europa","eur"},{"Asia Occidental","aso"},{"Asia", "asi"},{"Asia Central","asc"},
														{"Sur de Asia", "ass"},{"Sureste Asi�tico","sea"},{"Ocean�a","oce"},
														{"Norteam�rica","amn"},{"Centroam�rica","amc"},{"Suram�rica","ams"},
														{"Las Antillas","ant"},{"�frica","afr"},{"Otras Regiones","otr"},{"Onzas","oun"},
														{"Otros (monedas obsoletas)","obs"}};
			for (int i = 0; i < regiones.length; i++){
				html+="\t<optgroup label=\"" + regiones[i][0] + "\">\n\t\t\t\t\t\t\t";
				for (Object valor : valores) 
					for (String codigo : monedas.keySet())
						if (monedas.get(codigo).get(0).equals(valor) && monedas.get(codigo).get(1).equals(regiones[i][1])){
							html+="\t<option value=\"" + codigo+ "\"";
							if (codigo.equals(unidad)) // selecciona la clave de la unidad anterior
								html+=" selected";		// si es unidad1, por defecto "EUR"; unidad2 por defecto "USD"
							html+=">" + (String)valor +" ("+codigo+")</option>\n\t\t\t\t\t\t\t";
						}
				html+="\t<option disabled></option>\n\t\t\t\t\t\t\t</optgroup>\n\t\t\t\t\t\t";
			}
			return html;
		}
	%>
	<fieldset>
		<legend>Conversor de Divisas</legend>
		<form method="POST" action="ConversorDivisas">
			<input type="hidden" name="req" value="conversor" />
			Pasar de
			<table>
				<tr>						
					<td>
						<select id="moneda1" name="unidad1" onchange="seleccionaBandera1()">
						<%= escribeLista(monedas, valores, unidad1) %>
						</select>
					</td>
					<td width="135">a</td>
					<td>
						<select id="moneda2" name="unidad2" onchange="seleccionaBandera2()">
							<%= escribeLista(monedas, valores, unidad2) %>
						</select>
					</td>
				</tr>
				<tr>
					<td align="center"><img id="flag1" src="flags/<%=unidad1 %>.png" width="100" height="67"/></td>
					<td></td>
					<td align="center"><img id="flag2" src="flags/<%=unidad2 %>.png" width="100" height="67"/></td>
				</tr>
				<tr>
					<td align="center"><input type="text" size="7" name="numero" value=""/></td>
				</tr>
			</table>
			<br />
			<div class="botones"><input id="submit" type="submit" name="accion" value="Convertir" /></div>	
		</form>		
			<% if (request.getAttribute("error") != null)  // si hay un error, imprime este <div> con el error
						out.print("<div class=\"error\">" + request.getAttribute("error") + "</div>"); %>		
	</fieldset>
	<script>
		/* Script para mostrar la bandera de la moneda seleccionada */
		function seleccionaBandera1(){
			var element = document.getElementById("moneda1");
			var select = element.options[element.selectedIndex].value;
			document.getElementById("flag1").src = "flags/"+select+".png";
		}
		function seleccionaBandera2(){
			var element= document.getElementById("moneda2");
			var select = element.options[element.selectedIndex].value;
			document.getElementById("flag2").src = "flags/"+select+".png";
		}
	</script>
	<a href="registro.jsp" target="popup">Hist�rico</a>
	<a href="index.html">Volver</a>
</body>
</html>