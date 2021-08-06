# New Typesetting System (TeX)

This is GitHub copy of [the source code](https://ctan.org/tex-archive/systems/nts) of the 
[New Typesetting System](https://en.wikipedia.org/wiki/New_Typesetting_System),
    which is a Java reimplementation
    of Donald Knuth's original version of TeX.

This code was completed in 2001.
However, for various reasons it was decided not to continue the project (i.e., support more modern flavors of TeX),
	and there has been no further development on this code since.

This copy exists to:

1. Have a nice way to browse the source code of NTS, especially for those who are thinking of
    developing [new implementations of TeX](https://github.com/jamespfennell/texide).

2. Ensure that the code can still be compiled in modern versions of Java so this artifact
    will live on.

**Currently the code compiles successfully but NTS crashes when it starts.**
The jar that is [distributed on CTAN]((https://ctan.org/tex-archive/systems/nts)) does work.

## Compiling and running NTS

Disclaimer: these instructions were written by someone who never created a Java project from scratch before,
so they may be weird.

The code was written for Java 2 (aka 1.2) originally.
It has been successfully compiled with Java 16, and so should theoretically work for all
intermediate versions too.

**Compile**: From the repo root, 
```sh
javac -classpath src -d build src/Nts.java
jar --create --file nts.jar --main-class=Nts -C build Nts.class build/nts
```
The compiled artifact is `nts.jar`.

**Run**: Invoke `java -jar nts.jar`.

### Installing Java

- **MacOS**:
    Install OpenJDK using brew (`brew install java`) and then perform the sym-linking
     command as described in `brew info java`.

### Running the tests

Running the tests is really easy. Because there are no tests, to run the tests you just do nothing.

## License and changelog

NTS has a custom open source license
with extremely strange clauses around modification.

The code as hosted here has small modifications versus the original that are fully detailed in the changelog.
Specifically,
- The internal layout of the repository has been changed.
- Some warnings issued by recent Java compilers have been resolved by tweaks to the Java code.
- The code was formatted using the Google Java formatter.
