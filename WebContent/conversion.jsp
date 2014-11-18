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

		/* averiguamos si se han enviado los datos, para volverlos a poner */
		String numero;
		if (request.getParameter("numero") != null) // se ha enviado
			numero = request.getParameter("numero");
		else 
			numero="";	

		/* Recoge las unidades que se han enviado */
		String unidad1;
		String unidad2;		
		if (request.getParameter("accion").equals("Intercambiar") && request.getAttribute("error")==null) {
			/* Si no hay error hace el intercambio */
			unidad1 = request.getParameter("unidad2");
			unidad2 = request.getParameter("unidad1");
		} else {
			/* No se ha pulsado "Invertir" o ha habido un error */
			unidad1 = request.getParameter("unidad1");
			unidad2 = request.getParameter("unidad2");
		}			
	%>
	<%!
		public String escribeLista(HashMap<String, ArrayList<String>> monedas,
															ArrayList<String> valores, String unidad) {
			String html="";
			String[][] regiones = {{"Europa","eur"},{"Asia Occidental","aso"},{"Asia", "asi"},{"Asia Central","asc"},
														{"Sur de Asia", "ass"},{"Sureste Asiático","sea"},{"Oceanía","oce"},
														{"Norteamérica","amn"},{"Centroamérica","amc"},{"Suramérica","ams"},
														{"Las Antillas","ant"},{"África","afr"},{"Otras Regiones","otr"},{"Onzas","oun"},
														{"Otros (monedas obsoletas)","obs"}};
			for (int i = 0; i < regiones.length; i++){
				html+="\t<optgroup label=\"" + regiones[i][0] + "\">\n\t\t\t\t\t\t\t";
				for (Object valor : valores) 
					for (String codigo : monedas.keySet())
						if (monedas.get(codigo).get(0).equals(valor) && monedas.get(codigo).get(1).equals(regiones[i][1])){
							html+="\t<option value=\"" + codigo+ "\"";
							if (codigo.equals(unidad)) // selecciona la clave de la unidad anterior
								html+=" selected";		//
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
			<input type="hidden" name="req" value="conversion" />
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
					<td align="center"><img id="flag1" src="flags/<%=unidad1%>.png" width="100" height="67"/></td>
					<td>
						<input type="submit" name="accion" value="Intercambiar" />
					</td>
					<td align="center"><img id="flag2" src="flags/<%=unidad2 %>.png" width="100" height="67"/></td>
				</tr>
				<tr>
					<td align="center"><input type="text" size="7" name="numero" value="<%=numero %>"/></td>
					<td></td>
					<td align="center">
						<%
						/* Si se ha hecho la conversión se escribe el resultado */
							if (request.getAttribute("valorSalida") != null)
								out.println("<div class=\"result\">" + request.getAttribute("valorSalida") + " "
														+ unidad2+ "</div>");
						/* Si no se ha hecho la conversión y no hay error se muestra esto*/
							else if (numero!=""  && request.getAttribute("error")==null)
								out.println("<div class=\"result\">Cambio Incompatible</div>");
						%>
					</td>
				</tr>
			</table>
			<br />
			<input id="submit" type="submit" name="accion" value="Convertir" /><br />
			<input id="clean" type="submit" name="accion" value="Reiniciar" />		
		</form>
		<% 
				if (request.getAttribute("error") != null) 
					/* Si se ha producido un error se muestra este div con el error */
					out.print("<div class=\"error\">" + request.getAttribute("error") + "</div>");
				else if (request.getAttribute("valorSalida") != null)
					/* Si no hay error y se ha hecho la conversión */
					out.print("<div class=\"tasa\" title=\"1" + unidad1 + " = "
										+ request.getAttribute("tasaIntercambio") + " " + unidad2
										+ "\">Tasa de intercambio empleada</div>");
		%>
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
	<a href="registro.jsp" target="popup">Histórico</a>
	<a href="index.html">Volver</a>

</body>
</html>