# Questimator
Questimator (under development), is an application service that generates multiple-choice assessment questions for any topic contained within Wikipedia.

## How to get started?

### Installation

Prerequisites:
* [Java 1.8.x](https://www.oracle.com/technetwork/java/javase/downloads/index.html)
* [sbt build tool 1.2.8](https://www.scala-sbt.org/download.html)

### Running application

```
sbt run
```

### Example

Request:
```
curl -X POST http://localhost:9000/wiki/mcq -H 'Content-Type: application/json' -d '{ 
  "topic": "Machine learning" 
}'
```

Response:
```
{
    "topic": "Machine learning",
    "relatedTopics": [
        "algorithm",
        "supervised learning",
        "statistica",
        "artificial intelligence",
        "statistics",
        "mathematica",
        "neuron",
        "neural network",
        "data mining",
        "unsupervised learning"
    ]
}
```
