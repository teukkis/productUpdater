import static org.junit.Assert.*
import org.junit.Test


class UnitTest extends GroovyTestCase {

    GroovyShell shell = new GroovyShell()
    def httpTools = shell.parse(new File('BuildRestClient.groovy'))
    def tools = shell.parse(new File('../scripts/Main.groovy'))
    def restClient = httpTools.createRestClient()
    
    @Test
    void testProductByIdSuccess() {
        def productInformationCloudResponse = tools.getProductbyId( restClient["productInformationCloudClient"], restClient["productInformationCloudEndpoint"] )

        assertEquals("Check if status code is 200", 200, productInformationCloudResponse.statusCode)
        assertEquals("Confirm that the response content type is application/json", "application/json", productInformationCloudResponse.contentType)
        assertEquals("Check that the product code is still the same", "269112", productInformationCloudResponse.json.object.attributes.code.value[0])
    } 

    @Test
    void testProductByNonExistingProductId() {
        def productInformationCloudResponse = tools.getProductbyId( restClient["productInformationCloudClient"], restClient["productInformationCloudEndpointWrong"] )

        assertEquals("Check that query with a wrong product_id does not return any objects", 0, productInformationCloudResponse.json.object.size())
    }

    @Test
    void testAdditionalInfoIsValid() {
        def roiTestApiResponse = tools.getAdditionalInfo( restClient["roiTestApiClient"], restClient["roiTestApiClientEndpoint"], restClient["roiTestApiClientHeader"] )
        
        assertEquals("Check if status code is 200", 200, roiTestApiResponse.statusCode)
        assertEquals("Confirm that the response content type is text/xml", "text/xml", roiTestApiResponse.contentType) 
        assertNotNull(roiTestApiResponse.xml.maxInputVA) 
        assertNotNull(roiTestApiResponse.xml.tempMax)
        assertNotNull(roiTestApiResponse.xml.tempMin)
    }
}