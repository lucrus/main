/**
 *
 */
package com.lucrus.main.synchro;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author luca.russo
 */
public abstract class BaseContentHandler extends DefaultHandler {
    private String currentType;
    private String elementValue;
    private String openName;
    private String rootName;
    protected Object current;
//	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    /**
     *
     */
    public BaseContentHandler(String rootName) {
        super();
        this.rootName = rootName;
        this.current = null;
    }

//	protected Object parseWithNullType(String value, String type) {
//		if(type==null){
//			try {
//				return Integer.valueOf(value);
//			} catch(NumberFormatException nfe){}
//			try {
//				return Double.valueOf(value);
//			} catch(NumberFormatException nfe){}
//			try {
//				return sdf.parse(value);
//			} catch(ParseException dfe){
//				System.out.println(dfe);
//				
//			}
//			try {
//				if("true".equalsIgnoreCase(value)){
//					return true;
//				}
//				if("false".equalsIgnoreCase(value)){
//					return false;
//				}
//			} catch(Throwable nfe){}
//			try {
//				return value.toString();
//			} catch(NullPointerException npe){}
//		}
//		return null;
//	}
//	
//	protected Object parseWithType(String value, String type) {
//		if("Int64".equalsIgnoreCase(type) || "NUMERICO".equalsIgnoreCase(type) || type.contains("@")){
//			try {
//				return Long.valueOf(value);
//			} catch(NumberFormatException nfe){
//				try {
//					return Double.valueOf(value);
//				} catch(NumberFormatException nfe2){
//					throw new IllegalArgumentException("Error parsing element: received type " + type + " and value " + value);
//				}
//			}
//		}
//		
//		if("Double".equalsIgnoreCase(type)){
//			try {
//				return Double.valueOf(value);
//			} catch(NumberFormatException nfe2){
//				//throw new IllegalArgumentException("Error parsing element: received type " + type + " and value " + value);
//				return 0.0d;
//			}
//		}
//		
//		if("String".equalsIgnoreCase(type) || 
//		   "IMMAGINE".equalsIgnoreCase(type) ||
//		   "TESTO".equalsIgnoreCase(type) ||
//		   "MULTILINE".equalsIgnoreCase(type) ||
//		   "DOCUMENTO".equalsIgnoreCase(type) ||
//		   "MAPPA".equalsIgnoreCase(type)){
//			
//			return value;
//		}
//		if("boolean".equalsIgnoreCase(type)){
//			return Boolean.valueOf(value);
//		}
//		if("DateTime".equalsIgnoreCase(type) || "DATA".equalsIgnoreCase(type)){
//			try {
//				return sdf.parse(value);
//			} catch (ParseException e) {
//				throw new IllegalArgumentException("Error parsing element: received type " + type + " and value " + value);
//			}
//		}
//		return null;
//	}


    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        if (rootName.equals(localName)) {
            this.createResultList();
        } else {
            if (current == null) {
                this.current = this.createNewObject();
                this.addResultObject(this.current);
                this.currentType = null;
                this.openName = localName;
            } else {
                this.currentType = attributes.getValue("type");
            }
        }
        this.elementValue = null;
    }

    protected abstract void createResultList();

    protected abstract Object createNewObject();

    protected abstract void addResultObject(Object obj);

    protected abstract void setProperty(Object obj, String name, Object value, String type);


    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        if (localName.equals(this.openName)) {
            this.current = null;
        } else {
            if (this.elementValue != null && this.elementValue.trim().length() > 0) {
//				this.elementValue = Html.fromHtml(this.elementValue).toString();
//				Object value;
//				if(this.currentType==null || this.currentType.trim().length()==0){
//					value = this.parseWithNullType(this.elementValue, this.currentType);
//				} else {
//					value = this.parseWithType(this.elementValue, this.currentType);
//				}
                this.setProperty(this.current, localName, this.elementValue.trim(), this.currentType);

            }
        }
        this.currentType = null;
        this.elementValue = null;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
//		if (this.elementOn) {
        if (this.elementValue == null) {
            this.elementValue = new String(ch, start, length);
        } else {
            this.elementValue += new String(ch, start, length);
        }
//		}
    }
}
