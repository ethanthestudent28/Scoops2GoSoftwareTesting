# Software Testing & Quality Assurance 2CWK70 - Scoops2Go

This repository shows the test cases and unit tests created during my university assignment to test the Scoops2Go software. The software intentionally includes errors so that testing could reveal them.

Unit tests were writted using the JUnit and Mockito libraries. Other forms of testing are shown in the Test cases excel sheet.

## Your details

Name: Ethan Turner

Student ID: 22487557

## Summary of work

**Test Cases** are provided at the top level of the GitHub, in a file named `2CWK70 Test cases.xlsx`
The Test case file is separated into two main sheets:
- **unit_tests** contains all unit tests for the SUT. Test cases within this sheet may mention other sheets by name whenever their inputs are too large.
- **test_cases** contains all other test cases. These again may use other sheets for larger inputs, especially API inputs.

Implemented **Unit tests** are provided in the `src/test/java` folder. Test classes mirror the SUT file path to allow testing of protected methods.
`src/test/resources` contains a csv file used for one parameterised test. Method source test inputs are stored in the **TestMethodSourceProvider** class.

**Defect Reports** are available on GitHub, in the issues tab. Defect and security reports are written using issue templates, which are stored in the `.github/ISSUE_TEMPLATE` folder.

**Continuous Integration** is configured using GitHub Actions.
