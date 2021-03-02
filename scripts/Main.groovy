@Grab(group='com.github.groovy-wslite', module='groovy-wslite', version='1.1.3')
import wslite.rest.*
import wslite.http.auth.*
import groovy.json.*
import org.apache.commons.cli.Option


def getProductbyId(productInformationCloudClient, productInformationCloudEndpoint) {

    try {
        // GET request
        def productInformationCloudResponse = productInformationCloudClient.get(
            path: productInformationCloudEndpoint
        )

        return productInformationCloudResponse

    } catch(RESTClientException e) {
        println " "
        println "Exception occured while requesting the product by id"
        throw e
    } 
}


// Return a list, containing values of maxInputVA, tempMax, and tempMin
def getAdditionalInfo(roiTestApiClient, roiTestApiClientEndpoint, headers) {

    try {
        // GET request
        def roiTestApiResponse = roiTestApiClient.get(
            path: roiTestApiClientEndpoint, 
            headers: headers,
        )

        return roiTestApiResponse

    } catch(RESTClientException e) {
        println " "
        println "Exception occured while requesting enriching product info"
        throw e
    } 
}


def updateProduct(productInformationCloudClient, productInformationCloudEndpointForUpdate, informationToBeAdded) {

    def body = [
        attributes: 
        [
            "max_input_power_va": informationToBeAdded.max_input_power_va,
            "temperature_max": informationToBeAdded.temperature_max,
            "temperature_min": informationToBeAdded.temperature_min
            ]
        ]

    try {
        // PUT request
        def productInformationCloudResponse = productInformationCloudClient.put(path: productInformationCloudEndpointForUpdate) {
            {
              type ContentType.JSON
              json body
            }
        }

    } catch(RESTClientException e) {
        println " "
        println "Exception occured while requesting while updating the product"
        throw e
    } 
}


def printResults(oldProduct, updatedProduct) {

    try {
        println "-------------------------"
        println " "
        println "RESULTS:"
        println ""
        println "************ Old Product ************"
        println ""
        println "\tupdated " + oldProduct.attributes.modified_date.value[0]
        println "\tmax input power va: " + oldProduct.attributes.max_input_power_va.value[0]
        println "\tmaximum tempereture: " + oldProduct.attributes.temperature_max.value[0]
        println "\tminimum temperature: " + oldProduct.attributes.temperature_min.value[0]
        println "\tmodified by: " + oldProduct.attributes.modified_by.value[0]

        println " "
        println "************ Updated Product ************"
        println ""
        println "\tupdated " + updatedProduct.attributes.modified_date.value[0]
        println "\tmax input power va: " + updatedProduct.attributes.max_input_power_va.value[0]
        println "\tmaximum tempereture: " + updatedProduct.attributes.temperature_max.value[0]
        println "\tminimum temperature: " + updatedProduct.attributes.temperature_min.value[0]
        println "\tmodified by: " + updatedProduct.attributes.modified_by.value[0]
        println " "
        println "-------------------------"

    } catch (NullPointerException e) {
        throw e
    }
}


def runScript(product_id, username, password, token) {

    try {
        // Create new clients
        RESTClient productInformationCloudClient = new RESTClient("https://pim-try-api.productinformationcloud.com")
        RESTClient roiTestApiClient = new RESTClient("https://roitestapi.herokuapp.com")

        // Init endpoints
        def productInformationCloudEndpoint = "/di/object.json?className=/object/itemmaster/master/roima_demo/products&code=$product_id&~attributes=*"
        def roiTestApiClientEndpoint = "/tolerances/$product_id"

        // Token for authorized access in roiTestApi
        def headers = ["APITOKEN": token]
        
        // Add authorization info to the client to access products in productInformationCloud
        productInformationCloudClient.authorization = new HTTPBasicAuthorization(username, password.toString())

        // Define some variables
        def oldProduct
        def updatedProduct
        def id
        def productInformationCloudEndpointForUpdate

        // Get the product based its product_id
        oldProduct = getProductbyId(productInformationCloudClient, productInformationCloudEndpoint).json.object
        id = oldProduct.id[0]

        // Form a new endpoint based on the id of the product
        productInformationCloudEndpointForUpdate = "/di/object/$id"

        // Get the enriching information from roiTestApi
        roiTestApiResponseXml = getAdditionalInfo(roiTestApiClient, roiTestApiClientEndpoint, headers).xml
        def informationToBeAdded = [
          "max_input_power_va": roiTestApiResponseXml.maxInputVA.toString(),
          "temperature_max": roiTestApiResponseXml.tempMax.toString(),
          "temperature_min": roiTestApiResponseXml.tempMin.toString()
        ]

        // Update the product, and request that product
        updateProduct(productInformationCloudClient, productInformationCloudEndpointForUpdate, informationToBeAdded)
        updatedProduct = getProductbyId(productInformationCloudClient, productInformationCloudEndpoint).json.object

        // TEST, that the modification date has changed
        assert updatedProduct.attributes.modified_date.value[0] != oldProduct.attributes.modified_date.value[0]

        // Finally, print results to the console
        printResults(oldProduct, updatedProduct)

        return [ updatedProduct, oldProduct ]

    } catch (RESTClientException e) {
        println e.message
    }
}


// Command line client
// Require product_id, username and password
def startUi() {
    def cli = new CliBuilder(
        usage: 'Give -p product_id',
        footer: '\nThis tool will update the product based on the given product_id.\n'
    )

    cli.with {
        p(longOpt: 'product_id', 'product id', args: 1, required: true)
    }

    def option = cli.parse(args)

    if (!option) return
    if (option.h) cli.usage()

    def product_id = option.p

    println " "

    try {
        def console = System.console();

        if (console != null) {
            String token = console.readLine("Paste here a token for roiTestApi: ");
            String username = console.readLine("username: ");
            char[] password = console.readPassword("password: ")
            println " "
            println "Updating product ${product_id}... "
            println " "

            runScript(product_id, username, password, token)
        }
        
    } catch (Exception e) {
        println e
        println ""
    }
}


// START Script
startUi()
