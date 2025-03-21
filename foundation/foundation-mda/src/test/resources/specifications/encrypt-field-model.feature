@encryptfield
Feature: Encrypting record fields

  Background:
    Given the following dictionary types are defined:
      | name            | simpleType | driftPolicy      | ethicsPolicy             |
      | ssn             | string     |                  |                          |
      | phoneNumber     | string     |                  |                          |
      | singleSlaInDays | decimal    | oneZScorePolicy  |                          |
      | doubleSlaInDays | decimal    | twoZScoresPolicy |                          |
      | archivable      | boolean    |                  | sampleMinimumPolicy      |
      | gender          | string     |                  | genderDistributionPolicy |


