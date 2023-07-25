<h1 align="center">OTPinMaster</a></h1>
<h3 align="center">A highly customisable view for handling pin code and otp input with beautiful animations</h3>

<img src="https://github.com/matrobiyon/otp_pin_master/assets/111564722/81a7c048-93f4-43b6-b8d9-52b23c9145d8" height="700" align="center"/>
<img src="https://github.com/matrobiyon/otp_pin_master/assets/111564722/9c264a8c-f90c-4350-89d0-11214ccca1aa" height="700"  align="center"/>



## How to integrate into your app?
Integrating the project is simple. All you need to do is follow the below steps

Step 1. Add the JitPack repository to your build file. Add it in your root build.gradle at the end of repositories:

```java
allprojects {
  repositories {
    ...
    maven { url "https://jitpack.io" }
  }
}
```
Step 2. Add the dependency
```java
dependencies {
        implementation 'com.github.matrobiyon:otp_pin_master:1.1.1'
}
```

## How do you use the library in a simple way?
It appears that you have successfully integrated the library into your project, but what is the process for using it? Well, it's actually quite simple.
- Add the view in your xml file like

  ```
  <tj.otp.pin.master.OTPinMaster
        android:id="@+id/otp_pin_view"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:circleColor="#FFD470"
        app:activeRectangleColor="#FFD470"
        app:isPin="true"
        app:rectangleCount="5"
        app:rectangleWidth="65dp" 
  />
  ```
  In your code, create a variable and store the value of otp_pin_view in it.
  ```kotlin
  val otpPinView = findViewById<OTPinMaster>(R.id.otp__pin_view)
  ```
  or with binding
  ```
  val otpPinView = binding.otpPinView
  ```
  and set listener to it
  ```
  otpPinView.setOnDoneListener { result ->
    //Here should be your logic
  
    /*Check if result is correct
        otp.setIsCorrect(true)
                or
        otp.setIsCorrect(false)
     */
  }
  ```
  That's it. Congratulations 🥳

  This project is just the beginning and there's much more work to be done. I would greatly appreciate any contributions you can make. Your help will make a big difference!

  Feel free to explore the code, share your thoughts, and join me on this exciting journey. Together, we can create something amazing!

  Don't forget to bookmark my profile for future updates. Thank you for your support!

  Happy coding!

  Best regards,
  Qosim,


  ![flag-tajikistan_1f1f9-1f1ef](https://github.com/matrobiyon/otp_pin_master/assets/111564722/fa6f41f7-0b16-415e-85ae-9fc8c4b8ed47)

