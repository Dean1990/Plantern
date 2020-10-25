Plantern
---

![img](https://jitpack.io/v/Dean1990/Plantern.svg)

**Step 1.** Add it in your root build.gradle at the end of repositories:

```groovy
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

**Step 2.** Add the dependency

```groovy
dependencies {
	implementation 'com.github.Dean1990:Plantern:61b4985a1d'
}
```

**Step3.** Initialize Plantern

```kotlin
class App:Application() {

    override fun onCreate() {
        super.onCreate()

        Plantern.init(this)
    }
}
```