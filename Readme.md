# Aplikasi NewbieInFirebase
Aplikasi ini adalah sebuah aplikasi contoh untuk mahasiswa FTI UKSW dalam belajar menggunakan layanan Realtime Database milik Firebase.

## Beberapa hal yang perlu diperhatikan:
1. Buatlah project tersendiri di _**Firebase kalian masing-masing**_.
2. Jangan lupa copy file _google_services.json_ dari firebase project kalian ke Android project ini.

## Konfigurasi gradle yang perlu diperhatikan:
### Di Project Level
1. Tambahkan Library Google di dalam _**dependencies**_
    ```javascript
    classpath 'com.google.gms:google-services:4.3.3'
    ```
### Di app Level
1. Tambahkan plugin Google
    ```javascript
    apply plugin: 'com.google.gms.google-services'
    ```
2. Tambahkan library Realtime Database milik Firebase
    ```javascript
    implementation 'com.google.firebase:firebase-database:19.2.1'
    ```

# SELAMAT MENCOBA
