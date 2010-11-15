package xml;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import models.User;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.sun.org.apache.xalan.internal.xsltc.runtime.Attributes;

import play.Play;

public class XML_Parser extends DefaultHandler{
	
	String message= new String(); // containing error messages


	public String parse(File input) {
		//get a factory
		SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		try {

			//get a new instance of parser
			SAXParser parser = parserFactory.newSAXParser();

			//parse the file and also register this class for call backs
			parser.parse(input, this);

		}catch(Throwable t) {
			t.printStackTrace();
		}
		
		return message;
	}
		
		//Event Handlers
		public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
			//reset
			String tempValue = "";
			if(qName.equalsIgnoreCase("user")) {
				
//				tempEmp.setType(attributes.getValue("id"));
			}
		}


}


//		public void characters(char[] ch, int start, int length) throws SAXException {
//			tempVal = new String(ch,start,length);
//		}
//
//		public void endElement(String uri, String localName,
//			String qName) throws SAXException {
//
//			if(qName.equalsIgnoreCase("Employee")) {
//				//add it to the list
//				myEmpls.add(tempEmp);
//
//			}else if (qName.equalsIgnoreCase("Name")) {
//				tempEmp.setName(tempVal);
//			}else if (qName.equalsIgnoreCase("Id")) {
//				tempEmp.setId(Integer.parseInt(tempVal));
//			}else if (qName.equalsIgnoreCase("Age")) {
//				tempEmp.setAge(Integer.parseInt(tempVal));
//			}
//
//		}


//		DefaultHandler handler= new XML_Parser();
//		
//		try {
//			SAXParser saxParser= SAXParserFactory.newInstance().newSAXParser();
//			saxParser.parse(input, handler);
//		} catch(Throwable t){
//			t.printStackTrace();
//		}
////			catch (ParserConfigurationException e) {
////		}
////			// TODO Auto-generated catch block
////			e.printStackTrace();
////		} catch (SAXException e) {
////			// TODO Auto-generated catch block
////			e.printStackTrace();
////		} catch (IOException e) {
////			// TODO Auto-generated catch block
////			e.printStackTrace();
////		}
//		
//		// ---- SAX DefaultHandler methods ----
//
// static final String sNEWLINE = System.getProperty( "line.separator" );
// static private Writer out = null;
// private StringBuffer textBuffer = null;
//
// // ---- main ----
//
// public static void main( String[] argv )
// {
// if( argv.length != 1 )
// {
// System.err.println( "Usage: java ExampleSaxEcho MyXmlFile.xml" );
// System.exit( 1 );
// }
// try {
// // Use an instance of ourselves as the SAX event handler
// DefaultHandler handler = new ExampleSaxEcho();
// // Parse the input with the default (non-validating) parser
// SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
// saxParser.parse( new File( argv[0] ), handler );
// System.exit( 0 );
// } catch( Throwable t ) {
// t.printStackTrace();
// System.exit( 2 );
// }
// }
//
 // ---- SAX DefaultHandler methods ----
//
// @Override public void startDocument() throws SAXException
// {
// echoString( sNEWLINE + "<?xml ...?>" + sNEWLINE + sNEWLINE );
// }
//
// @Override public void endDocument() throws SAXException
// {
// echoString( sNEWLINE );
// }
//
// @Override
// public void startElement( String namespaceURI,
// String localName, // local name
// String qName, // qualified name
// Attributes attrs )
// throws SAXException
// {
// echoTextBuffer();
// String eName = ( "".equals( localName ) ) ? qName : localName;
// echoString( "<" + eName ); // element name
// if( attrs != null )
// {
// for( int i=0; i<attrs.getLength(); i++ )
// {
// String aName = attrs.getLocalName( i ); // Attr name
// if( "".equals( aName ) ) aName = attrs.getQName( i );
// echoString( " " + aName + "=\"" + attrs.getValue( i ) + "\"" );
// }
// }
// echoString( ">" );
// }
//
// @Override
// public void endElement( String namespaceURI,
// String localName, // local name
// String qName ) // qualified name
// throws SAXException
// {
// echoTextBuffer();
// String eName = ( "".equals( localName ) ) ? qName : localName;
// echoString( "</" + eName + ">" ); // element name
// }
//
// @Override
// public void characters( char[] buf, int offset, int len )
// throws SAXException
// {
// String s = new String( buf, offset, len );
// if( textBuffer == null )
// textBuffer = new StringBuffer( s );
// else
// textBuffer.append( s );
// }
//
// // ---- Helper methods ----
//
// // Display text accumulated in the character buffer
// private void echoTextBuffer()
// throws SAXException
// {
// if( textBuffer == null ) return;
// echoString( textBuffer.toString() );
// textBuffer = null;
// }
//
// // Wrap I/O exceptions in SAX exceptions, to
// // suit handler signature requirements
// private void echoString( String s )
// throws SAXException
// {
// try {
// if( null == out )
// out = new OutputStreamWriter( System.out, "UTF8" );
// out.write( s );
// out.flush();
// } catch( IOException ex ) {
// throw new SAXException( "I/O error", ex );
// }
// }
// }
