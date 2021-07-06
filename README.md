# MyBank Core Business Project

## Development Environment

This project has been developed using the following tools:

1. Eclipse IDE 2018-12 Java EE
2. Java JDK 1.8.0_191
3. Tomcat 8.5.68
4. Git Bash 2.12.1 (for Windows 10 environment)

## Testing Environments

1. Tested on Windows 10
2. Tested on Linux Ubuntu 18.04.4 LTS

These testing environments require previously described development environment for the applicable operatings system.

## How to Build

### Clone repository

```
git clone https://github.com/JuanfranMS/mybank-corebusiness.git mybank
cd mybank
cd ide
cd tomcat
(download and place here tomcat 8.5.68 for your operating system)
cd ..
cd jdk
(download and place here jdk1.8.0_xxx for your operating system)
cd ..
cd eclipse
(download and place here eclipse IDE 2018-12) (required to find ant tool)
cd ../..
```

Make sure scripts have executable permissions:

```
chmod u+x *.sh
```

### Compile back-end

```
. ./env.sh
cd workspace/mybank-corebusiness-backend
ant
ant -f build_war.xml
```

### Install back-end

```
cp mybank-corebusiness-backend.war ../../ide/tomcat/webapps
cd ../..
```

### Launch back-end

```
cd ide/tomcat/bin
./startup.sh
cd ../../..
```

### Compile Acceptance Tests

```
cd workspace/mybank-corebusiness-sdk-test
ant
```

### Run Acceptance Tests

```
ant TransactionTest
```

### Re-run Acceptance Tests

Note: in order to run tests again tomcat must be restarted because of the in-memory database.

```
cd ../../ide/tomcat/bin
./shutdown.sh
./startup.sh
```
