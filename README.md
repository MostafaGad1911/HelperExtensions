# Helper Extensions
Extensions 

[![](https://jitpack.io/v/MostafaGad1911/HelperExtensions.svg)](https://jitpack.io/#MostafaGad1911/HelperExtensions)

Combine some useful exts in One place

# Examples :
``` kotlin   
            this.errorMsg(msg = "Test error" , duration = 6000)
            this.customToast("test custom toast" , 6000)
	    this.GetLocationData(30.6426265,31.0908323)  // CityName = Madinet Berkat as Sabee , CountryName = Egypt , StateName = Menofia Governorate , CountryCode = EG
            "124343".StrongPass() // true | false
            "abs".Equals("ab") // true | false
            "gad!gmail.com".isValidEmail()  // true | false
	    NavController.NavigateUp(bundle , options)  
	    NavController.RefreshCurrentFragment()
	    
	    // this refer to context use requireActivity() in fragment
            this.getNext7Days()
     

```


# Getting Started 
## Step 1: Add it to build.gradle (project level) at the end of repositories:

 ``` kotlin  
             allprojects 
               {
	              repositories 
		           {	
			           maven { url 'https://jitpack.io' }
		           }  
	           }
```          
        

## Step 2 : Add the dependency
 ``` kotlin  
        implementation 'com.github.MostafaGad1911:HelperExtensions:1.1.0'
        
```         
