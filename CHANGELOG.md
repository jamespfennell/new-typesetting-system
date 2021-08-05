# Changelog

## 2021-08-05

- Added a README to the repo.

- Fixed 8 deprecation warnings, all involving use of a constructor to `Integer`;
    replaced these with `Integer.valueOf`.

- Formatted the Java files using the [Google Java formatter](https://github.com/google/google-java-format).
    This involved installing the formatter (`brew install google-java-format`) and then running it
    with `google-java-format -i src/nts/*/*`.

## 2021-08-04

Created the repo by downloading [the code from CTAN](https://ctan.org/tex-archive/systems/nts?lang=en).

The CTAN download contains compiled artifacts as well as source code, and everything is laid out
    kind of randomly.
The compiled artifacts were discarded, and other files copied over to the Git repo using the following commands:

```sh
mv $NTS_ROOT/texmf/doc/nts $GIT_ROOT/doc        
mkdir $GIT_ROOT/scripts
mv $NTS_ROOT/texmf/nts/perltk/* $GIT_ROOT/scripts
mv $NTS_ROOT/texmf/nts/scripts/* $GIT_ROOT/scripts
mv $NTS_ROOT/texmf/source/nts/nts-1.00-beta $GIT_ROOT/src
```
