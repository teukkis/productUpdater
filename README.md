### Run the script with a correct product_id
1. `groovy ./scripts/Main.groovy -p 269112`
2. Input username and password to access productInformationCloud


### Run unit tests
1. Change valid username and password ( BuildRestClient.groovy, lines 8 and 9 ) 
2. `groovyc TestRunner.groovy UnitTest.groovy BuildRestClient.groovy && groovy TestRunner`
