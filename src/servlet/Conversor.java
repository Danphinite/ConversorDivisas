package servlet;

import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import moneda.Moneda;

/**
 * Servlet implementation class Conversor
 */
@WebServlet("/ConversorDivisas")
public class Conversor extends HttpServlet {
	private static final long serialVersionUID = 1L;


    /**
     * @see HttpServlet#HttpServlet()
     */
    public Conversor() {
        super();
    } 
    	
    	/**
    	 * Se creará un HashMap con información sobre todas las monedas
    	 * y se cargará en contexto
    	 */
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		ServletContext contexto = config.getServletContext();
		HashMap<String, ArrayList<String>> monedas = leeFichero();
		
		/* El siguiente método ordena los valores del mapa "monedas" por orden alfabético */
		ArrayList<String> valores = new ArrayList<String>();
		for (String moneda : monedas.keySet()) {
			valores.add(monedas.get(moneda).get(0));
		}
		Collections.sort(valores, new Comparator<String>() {
	        public int compare(String o1, String o2) {
	            return o1.compareTo(o2);
	        }
	    });
		/* Se carga en contexto el listado con las monedas y los nombres de monedas ordenados */
		contexto.setAttribute("monedas", monedas);
		contexto.setAttribute("valores", valores);
	}
	
	/**
	 * Método que lee el fichero con el listado de todos los códigos,
	 * nombre de moneda y región, devolviendo un HashMap de ArrayList
	 * @return HashMap
	 */
	private HashMap<String, ArrayList<String>> leeFichero() {
		HashMap<String, ArrayList<String>> monedas = new HashMap<String, ArrayList<String>>();
		String archivo = null;
		FileReader fr = null;
		BufferedReader br = null;

		try {
			/* Apertura del fichero y creación de BufferedReader 
			 *  para poder hacer la lectura
			 * */
			archivo = this.getServletContext().getRealPath("listadoBueno.txt"); // pasa la ruta absoluta del fichero
			fr = new FileReader(archivo); 
			br = new BufferedReader(fr);

			/* Lectura del fichero */
			String linea;
			while ((linea = br.readLine()) != null) {
				ArrayList<String> moneda = new ArrayList<String>();
				String codigo = linea.split("-")[0];
				String valor = linea.split("-")[1];
				String region = linea.split("-")[2];
				moneda.add(valor); // nombre de la moneda
				moneda.add(region); // región de la moneda (ej: 'eur')
				monedas.put(codigo, moneda);
				}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			/* En el finally cerramos el fichero, para asegurarnos
			  * que se cierra tanto si todo va bien como si salta
			  * una excepción
			  * */
			try {
				if (null != fr) {
					fr.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return monedas;
		}
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost (request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String oper = request.getParameter("accion");
		String nuevaRuta = "";	
		try {
			switch (oper) {
				
				case "Convertir":
					if (validaNumero(request))  { // el número es válido
						conversor(request);
						nuevaRuta = "conversion.jsp";
					} else
						nuevaRuta = 	request.getParameter("req")+".jsp";
					break;
					
				case "Intercambiar":
					if (validaNumero(request))  {
						inversor(request);
						nuevaRuta = "conversion.jsp";
					} else
						nuevaRuta = 	request.getParameter("req")+".jsp";
					break;
				
				case "Reiniciar":
					nuevaRuta = "conversor.jsp";
					break;
					
				default: 
					nuevaRuta="conversor.jsp";
					break;
			}
		} catch (NullPointerException ex) {
			nuevaRuta = "conversor.jsp";
		}
			request.getRequestDispatcher(nuevaRuta).forward(request, response);
	}
	
	/**
	 * Verifica que se ha introducido un carácter numérico
	 * @param request
	 * @return boolean
	 */
	public boolean validaNumero (HttpServletRequest request) {
		String numero = (String) request.getParameter("numero");
		try {	
			Double.parseDouble(numero);
		} catch (NumberFormatException n) {
			// si el carácter no es válido, devuelve un error
			request.setAttribute("error", "Error al introducir el número");
			return false;
		} catch (NullPointerException e) {
			return false;
		}
		return true;
	}
	
	/**
	 * Método que crea un objeto de tipo Moneda,
	 * a partir de los parámetros enviados desde jsp
	 * @param request
	 */
	public void conversor (HttpServletRequest request) {
		String numero = request.getParameter("numero");
		String unidad1 = request.getParameter("unidad1");
		String unidad2 = request.getParameter("unidad2");
		Moneda moneda = new Moneda (Double.parseDouble(numero), unidad1, unidad2);
		DecimalFormat df = new DecimalFormat("#.###"); // Formato con 3 decimales
		double valorSalida = moneda.convertir(numero);
		if (valorSalida != 0) // Se ha hecho un cambio de unidades
			request.setAttribute("valorSalida", df.format(valorSalida));
		else // No existe la tasa de intercambio entre las unidades
			request.setAttribute("valorSalida", null);
		request.setAttribute("tasaIntercambio", moneda.getFactorConversion());
		registrar(request, moneda); // registra la conversión en un Array
	}
	
	/**
	 * Método que crea un objeto de tipo Moneda,
	 * a partir de los parámetros enviados desde jsp,
	 * con las unidades intercambiadas
	 * @param request
	 */
	public void inversor (HttpServletRequest request) {
		String numero = request.getParameter("numero");
		String unidad1 = request.getParameter("unidad2");
		String unidad2 = request.getParameter("unidad1");		
		Moneda moneda = new Moneda (Double.parseDouble(numero), unidad1, unidad2);
		DecimalFormat df = new DecimalFormat("#.###"); // Formato con 3 decimales
		double valorSalida = moneda.convertir(numero);
		if (valorSalida != 0)
			request.setAttribute("valorSalida", df.format(valorSalida));
		else
			request.setAttribute("valorSalida", null);
		request.setAttribute("tasaIntercambio", moneda.getFactorConversion());
		registrar(request, moneda); // registra la conversión en un Array
	}
	
	/**
	 * Método que hace un registro de todas las conversiones realizadas
	 * y las guarda en la sesión
	 * @param request
	 * @param moneda
	 * @return 
	 */
	public void registrar(HttpServletRequest request, Moneda moneda) {
		HttpSession sesion = request.getSession();
		ArrayList<Moneda>registro = (ArrayList<Moneda>) sesion.getAttribute("registro");
		if (registro == null) // no existe un registro
			registro = new ArrayList<Moneda>(); // se crea el registro
		registro.add(moneda);
		sesion.setAttribute("registro", registro);
	}
}
