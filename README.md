# ShrooML Android Application

## Authors
- Maelig Pesantez (Maelig.Pesantez.Etu@univ-lemans.fr, @e2103064)  
- Luka Cognard (Luka.Cognard.Etu@univ-lemans.fr, @s2200371)  
- Mewen Puren (Mewen.Puren.Etu@univ-lemans.fr, @s201509)  
- Bilal Mezrhab (Bilal.Mezrhab.Etu@univ-lemans.fr, @s2201668)

## Supervisors
- Aghilas Sini (Aghilas.Sini@univ-lemans.fr, @asini)  
- Frédéric Lagrange (Frederic.Lagrange@univ-lemans.fr, @flagrange)  

## Project
**ShrooML** is an Android application developed by three first-year Master AI students at Le Mans University (Maelig Pesantez, Luka Cognard, and Mewen Puren) as part of the DevOps module in the second semester of M1.  

The goal of ShrooML is simple: recognize mushroom species from photos using our previously developed **AutoML** package (now at [https://github.com/ShrooML-Team/AutoML](https://github.com/ShrooML-Team/AutoML)), and allow user interaction via quizzes and user-submitted observations with confidence scores.  

**Date:** February 2026 – April 2026

---

## Overview

ShrooML is an Android application with the following features:  

- **Automatic mushroom species recognition** from user-uploaded photos using the AutoML Python package  
- **User quizzes** to rank species knowledge on our annotated dataset  
- **Community recognition scoring**, allowing users to submit observations and see confidence scores  
- **DevOps pipeline ready** for CI/CD integration (GitHub Actions to be implemented)  

The project is also an opportunity to explore mobile application development while reusing and extending our AutoML package.  

---

## Features

- **Photo-based mushroom identification**  
- **Interactive quizzes** among users for ranking and learning  
- **Confidence scoring** for user-submitted identifications  
- **Integration with AutoML package** for automated machine learning pipelines  
- **Planned testing suite** to ensure reliability and correctness  
- **CI/CD ready architecture** for future pipeline automation  

---

## Installation (Development / Build)

> ⚠️ The project is in early development. Installation instructions are provisional.

```bash
git clone https://github.com/ShrooML-Team/ShrooML.git
cd ShrooML
```

- Open the project in **Android Studio**  
- Sync Gradle and build the application  
- Run on an Android emulator or physical device  

> APK installation instructions will be provided once the first build is stable.

---

## Minimal Usage

The application will be launched on a device/emulator, and users can:  

1. Take a photo of a mushroom for automatic recognition  
2. Participate in quizzes to compare knowledge on annotated datasets  
3. Submit observations and see confidence scores  

---

## Base Datasets

The application uses the following datasets for model training and quizzes:

- **Kaggle Mushroom Classification Dataset**: [https://www.kaggle.com/datasets/uciml/mushroom-classification](https://www.kaggle.com/datasets/uciml/mushroom-classification)
- **kaggle Mushroom Image By Specie Dataset**: [https://www.kaggle.com/datasets/thehir0/mushroom-species?select=dataset](https://www.kaggle.com/datasets/thehir0/mushroom-species?select=dataset)

---

## Testing

TODO

---

## Project Structure

TODO

---

## Documentation

Documentation will be provided as the project develops. Currently, the AutoML package documentation is available at [https://automl-group-13.netlify.app](https://automl-group-13.netlify.app).  

---

## Contributing

- Only group members can contribute  
- All contributions must follow existing coding style and pass tests  

---

## Commit Conventions

```bash
<type>([<scope>]): <description>
```

| Type      | Description |
|-----------|-------------|
| feat      | Adding a new feature |
| fix       | Fixing a bug |
| docs      | Documentation changes |
| style     | Code style changes |
| refactor  | Code refactoring |
| perf      | Performance improvement |
| test      | Add or update tests |
| chore     | Maintenance tasks |
| ci        | Continuous Integration config changes |
| revert    | Revert a previous commit |
| release   | Release a new version |
| hotfix    | Critical bug fix |
| init      | Initial commit |

---

## License

Copyright (c) 2026 Maelig Pesantez, Luka Cognard, Mewen Puren  

Academic purposes only. Provided "AS IS", without warranty of any kind.  
Commercial use is strictly prohibited.  

---

## Useful Links
- [AutoML Python Package](https://github.com/ShrooML-Team/AutoML)  
- [Initial Project Description]()

---

## Contact

| Role        | Name            | Email                               | University Identifier   |
|-------------|----------------|-------------------------------------|--------------|
| Developer   | Maelig Pesantez | maelig.pesantez.Etu@univ-lemans.fr | @e2103064    |
| Developer   | Luka Cognard   | Luka.Cognard.Etu@univ-lemans.fr   | @s2200371    |
| Developer   | Mewen Puren    | Mewen.Puren.Etu@univ-lemans.fr    | @s201509     |
| Developer   | Bilal Mezrhab   | Bilal.Mezrhab.Etu@univ-lemans.fr   | @s2201668    |
| Supervisor  | Aghilas Sini   | Aghilas.Sini@univ-lemans.fr       | @asini       |
| Supervisor  | Frédéric Lagrange | Frederic.Lagrange@univ-lemans.fr | @flagrange   |
