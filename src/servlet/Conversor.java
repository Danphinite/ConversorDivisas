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
        // TODO Auto-generated constructor stub
    } 
    	
    	/**
    	 * Se crear� un HashMap con informaci�n sobre todas las monedas
    	 * y se cargar� en contexto
    	 */
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		ServletContext contexto = config.getServletContext();
		HashMap<String, ArrayList<String>> monedas = leeFichero2();
		
		/* El siguiente m�todo ordena los valores del mapa "monedas" por orden alfab�tico */
		ArrayList<String> valores = new ArrayList<String>();
		for (String moneda : monedas.keySet()) {
			valores.add(monedas.get(moneda).get(0));
		}
		Collections.sort(valores, new Comparator<String>() {
	        public int compare(String o1, String o2) {
	            return o1.compareTo(o2);
	        }
	    });
		contexto.setAttribute("monedas", monedas);
		contexto.setAttribute("valores", valores);
	}
	
	/**
	 * Segundo m�todo que lee el fichero con el listado de todos los c�digos,
	 * nombres de monedas y regi�n, devolviendo un HashMap de ArrayList
	 * @return HashMap
	 */
	private HashMap<String, ArrayList<String>> leeFichero2() {
		HashMap<String, ArrayList<String>> monedas = new HashMap<String, ArrayList<String>>();
		File archivo = null;
		FileReader fr = null;
		BufferedReader br = null;

		try {
			/* Apertura del fichero y creaci�n de BufferedReader para poder
			 *  hacer la lectura (disponer del m�todo readLine())
			 * */
			archivo = new File("/Users/DAW 2� - Ma�ana/git/ConversorDivisas/WebContent/listadoBueno.txt");
			fr = new FileReader(archivo.getAbsolutePath()); // pasa la ruta absoluta del fichero
			br = new BufferedReader(fr);

			/* Lectura del fichero */
			String linea;
			while ((linea = br.readLine()) != null) {
				ArrayList<String> moneda = new ArrayList<String>();
				String codigo = linea.split("-")[0];
				String valor = linea.split("-")[1];
				String region = linea.split("-")[2];
				moneda.add(valor);
				moneda.add(region);
				monedas.put(codigo, moneda);
				}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			/* En el finally cerramos el fichero, para asegurarnos
			  * que se cierra tanto si todo va bien como si salta
			  * una excepci�n
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
	 * Lee el fichero con el listado de todos los c�digos
	 * y nombres de monedas, devolviendo un HashMap
	 * @return HashMap
	 */
	public HashMap<String, String> leeFichero () {
		HashMap<String, String> monedas = new HashMap<String, String>();
		String archivo = null;
		FileReader fr = null;
		BufferedReader br = null;

		try {
			/* Apertura del fichero y creaci�n de BufferedReader para poder
			 *  hacer la lectura (disponer del m�todo readLine())
			 * */
			archivo = this.getServletContext().getRealPath("listadoBueno.txt");
			fr = new FileReader(archivo); // pasa la ruta absoluta del fichero
			br = new BufferedReader(fr);

			/* Lectura del fichero */
			String linea;
			while ((linea = br.readLine()) != null) {
				String codigo = linea.split("-")[0];
				String moneda = linea.split("-")[1];
				String region = linea.split("-")[2];
				monedas.put(codigo, moneda);
				}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			/* En el finally cerramos el fichero, para asegurarnos
			  * que se cierra tanto si todo va bien como si salta
			  * una excepci�n
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
					if (validaNumero(request))  {
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
					//request.logout();
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
	 * Verifica que se ha introducido un car�cter num�rico
	 * @param request
	 * @return boolean
	 */
	public boolean validaNumero (HttpServletRequest request) {
		String numero = (String) request.getParameter("numero");
		try {	
			Double.parseDouble(numero);
		} catch (NumberFormatException n) {
			// si el car�cter no es v�lido, devuelve un error
			request.setAttribute("error", "Error al introducir el n�mero");
			return false;
		} catch (NullPointerException e) {
			return false;
		}
		return true;
	}
	
	/**
	 * M�todo que crea un objeto de tipo Moneda,
	 * a partir de los par�metros enviados desde jsp
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
		registrar(request, moneda); // registra la conversi�n en un Array
	}
	
	/**
	 * M�todo que crea un objeto de tipo Moneda,
	 * a partir de los par�metros enviados desde jsp,
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
		registrar(request, moneda); // registra la conversi�n en un Array
	}
	
	/**
	 * M�todo que hace un registro de todas las conversiones realizadas
	 * y las guarda en la sesi�n
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
