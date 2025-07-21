# Markdown Files Categorization Plan

## Overview
This document outlines the categorization and recommended actions for all markdown files in the Latte Plugin repository.

## Categorization and Actions

### Core Documentation
| File | Action | Reason |
|------|--------|--------|
| README.md | Keep and enhance | Main documentation file that should be enhanced with relevant information from other files |
| TESTING.md | Move to docs/user | User-focused testing instructions that should be preserved |

### Development Setup Documentation
| File | Action | Reason |
|------|--------|--------|
| JAVA_INSTALL.md | Move to docs/setup | Detailed Java installation instructions that are still relevant |
| GRADLE_7.6_INSTALL.md | Move to docs/setup | Detailed Gradle installation instructions that are still relevant |
| BUILD_AND_TEST.md | Move to docs/setup | Comprehensive build and test instructions that are still relevant |

### Feature Implementation Documentation
| File | Action | Reason |
|------|--------|--------|
| LATTE_4.0_SUPPORT.md | Move to docs/development | Implementation details for Latte 4.0+ support, useful for developers |
| NSYNTAX_ATTRIBUTE_SUPPORT.md | Move to docs/development | Implementation details for n:syntax attribute, useful for developers |
| PERFORMANCE_OPTIMIZATIONS.md | Move to docs/development | Implementation details for performance optimizations, useful for developers |
| NETTE_PACKAGE_SUPPORT.md | Move to docs/development | Implementation details for Nette package support, useful for developers |
| VERSION_SUPPORT.md | Move to docs/development | Details about version support implementation, useful for developers |

### Testing Documentation
| File | Action | Reason |
|------|--------|--------|
| TEST_COVERAGE.md | Move to docs/testing | Overview of test coverage, useful for developers |
| TEST_ISSUES.md | Remove | Historical record of test issues that have been resolved |
| TEST_CONFIGURATION.md | Move to docs/testing | Test configuration details that are still relevant |
| RUNNING_TESTS_IN_INTELLIJ.md | Move to docs/testing | Instructions for running tests in IntelliJ that are still relevant |

### Issue/Solution Documentation
| File | Action | Reason |
|------|--------|--------|
| ENVIRONMENT_ERRORS_SOLUTION.md | Remove | Solution for environment errors that have been resolved |
| LANGUAGE_INJECTOR_FIX.md | Remove | Solution for language injector issues that have been resolved |
| LANGUAGE_INJECTOR_SOLUTION.md | Remove | Another solution for language injector issues that have been resolved |
| PERFORMANCE_TEST_FIXES.md | Remove | Fixes for performance tests that have been implemented |
| PERFORMANCE_TEST_FIXES.txt | Remove | Duplicate of PERFORMANCE_TEST_FIXES.md |

### Summary and Planning Documentation
| File | Action | Reason |
|------|--------|--------|
| SUMMARY.md | Consolidate into README.md | Overview of the project implementation that can be incorporated into README.md |
| SOLUTION_SUMMARY.md | Remove | Summary of JUnit 4 migration solution that has been implemented |
| FUTURE_ENHANCEMENTS.md | Move to docs/development | Roadmap for future enhancements, useful for developers |
| CHANGES_SUMMARY.md | Remove | Summary of changes that have been implemented |

### Build Results and Reports
| File | Action | Reason |
|------|--------|--------|
| BUILD_RESULTS.md | Remove | Build results report that is no longer relevant |
| build_test_results.md | Remove | Another build results report that is no longer relevant |

## Summary of Actions
- **Keep and enhance**: 1 file
- **Move to docs folder**: 12 files
- **Consolidate into README.md**: 1 file
- **Remove**: 10 files

## Next Steps
1. Create the docs folder structure (docs/user, docs/setup, docs/development, docs/testing)
2. Update README.md with consolidated information from SUMMARY.md
3. Move the relevant files to their respective folders
4. Update cross-references between files
5. Remove the files marked for removal