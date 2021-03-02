@Grab(group='com.github.groovy-wslite', module='groovy-wslite', version='1.1.3')
import wslite.rest.*
import wslite.http.auth.*


def createRestClient() {

    def username = "<USERNAME>"
    def password = "<PASSWORD>"

    RESTClient productInformationCloudClient = new RESTClient("https://pim-try-api.productinformationcloud.com")
    RESTClient roiTestApiClient = new RESTClient("https://roitestapi.herokuapp.com")

    def product_id = 269112
    def wrong_product_id = 123456789

    // Init endpoints
    def productInformationCloudEndpoint = "/di/object.json?className=/object/itemmaster/master/roima_demo/products&code=$product_id&~attributes=*"
    def productInformationCloudEndpointWrong = "/di/object.json?className=/object/itemmaster/master/roima_demo/products&code=$wrong_product_id&~attributes=*"

    def roiTestApiClientEndpoint = "/tolerances/$product_id"

    // Token for authorized access in roiTestApi
    def roiTestApiClientHeader = ["APITOKEN": "84f1ee1dbdd2948b0106d5a302d7ed37ea3b8c4bd4a3b65bf626fceb24477e03"]

    // Add authorization info to the client to access products in productInformationCloud
    productInformationCloudClient.authorization = new HTTPBasicAuthorization(username, password)

    def restClient = [
        "productInformationCloudClient": productInformationCloudClient, 
        "productInformationCloudEndpoint": productInformationCloudEndpoint,
        "productInformationCloudEndpointWrong": productInformationCloudEndpointWrong,
        "roiTestApiClient": roiTestApiClient,
        "roiTestApiClientEndpoint": roiTestApiClientEndpoint,
        "roiTestApiClientHeader": roiTestApiClientHeader
    ]

return restClient

}