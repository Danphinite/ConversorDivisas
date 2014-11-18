package moneda;

import java.io.IOException;
import java.io.Serializable;
import java.text.DecimalFormat;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Clase Moneda, con las unidades monetarias y la tasa de intercambio.
 * Extiende de DefaultHandler para obtener la tasa desde WSDL
 * @author Daniel
 *
 */
public class Moneda extends DefaultHandler implements Serializable {
	/**
	 * Clase serializable para guardar en sesión
	 */
	private static final long serialVersionUID = 1L;
	double valorEntrada, valorSalida, factorConversion, factorAntiguo;
	String unidad1, unidad2;
	Boolean doubleTag = false;
	
	/**
	 * El constructor invoca al método de lectura de XML online
	 */
	public Moneda (double valorEntrada, String unidad1, String unidad2) {
		this.valorEntrada = valorEntrada;
		this.unidad1 = unidad1;
		this.unidad2 = unidad2;
		leeXML();
	}
	
	/**
	 * Se sobrecarga el constructor del puntero para que el parser capture
	 * el elemento del XML en el WSDL (double)
	 */
	public void leeXML() {
		
		try {
			DefaultHandler ficheroXML = new DefaultHandler(){
				@Override
				public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {  
					if (qName.equals("double")) {
						doubleTag = true;
					}
				}  
				@Override
				public void characters(char ch[], int start, int length) throws SAXException {  
					if (doubleTag) {  
						factorConversion = Double.parseDouble(new String(ch, start, length));
					}  
				}
				@Override
				public void endElement(String uri, String localName, String qName) throws SAXException {  
					if (qName.equals("double")) {  
						doubleTag = false;  
					} 
				}
			};
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser builder = factory.newSAXParser();
			builder.parse("http://webservicex.net/currencyconvertor.asmx/ConversionRate?FromCurrency="
					+ unidad1 + "&ToCurrency="+ unidad2, ficheroXML);		
		} catch (SAXException e) {
		} catch (ParserConfigurationException e) {
		} catch (IOException e) {
		}
	}
	
	/**
	 * Segundo método para obtener la tasa de intercambio
	 * de monedas antiguas u obsoletas
	 */
	public double leeXML(String codigo1, String codigo2) {
		factorAntiguo = 0;
		try {
			DefaultHandler ficheroXML = new DefaultHandler(){
				@Override
				public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {  
					if (qName.equals("double")) {
						doubleTag = true;
					}
				}
				@Override
				public void characters(char ch[], int start, int length) throws SAXException {  
					if (doubleTag) {  
						factorAntiguo = Double.parseDouble(new String(ch, start, length));
					}  
				} 
				@Override
				public void endElement(String uri, String localName, String qName) throws SAXException {  
					if (qName.equals("double")) {  
						doubleTag = false;  
					} 
				}
			};
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser builder = factory.newSAXParser();
			builder.parse("http://webservicex.net/currencyconvertor.asmx/ConversionRate?FromCurrency="
					+ codigo1 + "&ToCurrency="+ codigo2, ficheroXML);		
		} catch (SAXException e) {
		} catch (ParserConfigurationException e) {
		} catch (IOException e) {
		}
		return factorAntiguo;
	}
	
	/**
	 * Devuelve el valor convertido desde la cantidad dada
	 * @param numero de entrada
	 * @return double
	 */
	public double convertir (String numero) {
		if (factorConversion != 0) // Existe una tasa para las unidades
			return valorSalida = Double.parseDouble(numero) * factorConversion;
		else 
			return valorSalida = Double.parseDouble(numero) * otrasUnidades(numero);			
	}
	
	/**
	 * Método manual para devolver la tasa de intercambio
	 * de monedas obsoletas
	 */
	public double otrasUnidades(String numero){
		if (unidad1.equals("NBK"))
			return factorConversion = 2.66 * leeXML("EUR", unidad2);
		if (unidad2.equals("NBK"))
			return factorConversion = leeXML(unidad1, "EUR") / 2.66;
		if (unidad1.equals("RMU"))
			return factorConversion = (leeXML("XAG", "EUR")*0.13757*25) * leeXML("EUR", unidad2);
		if (unidad2.equals("RMU"))
			return factorConversion = leeXML(unidad1, "EUR") / (leeXML("XAG", "EUR")*0.13757 * 25);
		if (unidad1.equals("RMD"))
			return factorConversion = (leeXML("XAG", "EUR")*0.13757) * leeXML("EUR", unidad2);
		if (unidad2.equals("RMD"))
			return factorConversion = leeXML(unidad1, "EUR") / (leeXML("XAG", "EUR")*0.13757);
		return 0;
	}
	
	public double getValorEntrada() {
		return valorEntrada;
	}

	public double getValorSalida() {
		return valorSalida;
	}

	public String getUnidad1() {
		return unidad1;
	}

	public String getUnidad2() {
		return unidad2;
	}

	public String getFactorConversion() {
		DecimalFormat df = new DecimalFormat("#.####"); // Formato con 4 decimales
		return df.format(factorConversion);
	}
}
