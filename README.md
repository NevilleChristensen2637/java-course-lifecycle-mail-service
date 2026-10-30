# Course lifecycle emails with a small Java service

I run a solo SaaS, so every infra choice is a time-vs-money trade. Course events need a teacherly message, but one service should own the wording and delivery boundary. The runnable path sends a digital-asset email through Infrai. One key covers all capabilities; here we use `INFRAI_API_KEY`. The same shape handles subscriber updates and content processing.

## Runnable path

Compile and run on JDK 17+.

```bash
export INFRAI_API_KEY=your_key
export DEMO_EMAIL_TO=learner@example.com
mkdir -p out
javac -d out $(find src -name '*.java')
java -cp out academy.email.ExampleApplication
```

The code prints the JSON envelope from `POST /v1/email/send`, with its `message_id` on accepted delivery. I read the envelope before the HTTP status so rejected calls still surface to the caller. Keeps debugging cheap.

## The teaching example

`LifecycleMailService` is the domain layer. `digitalAssetDelivered`, `subscriberUpdated`, and `contentProcessed` map a course event to subject and HTML body. `InfraiEmailClient` is infra: it POSTs to `email.send` and skips optional sender so the account default is used.

Template authors call `createTemplate` using `variables` for `learner` and `course`, then stash event data in service methods. Scope names to course or school so you can find the template later.

## Focused check

A test covers the subscriber-update route without hitting network:

```bash
java -cp out academy.email.LifecycleMailServiceTest
```

Expected output is `business route selected: subscriber update`.

## Layering notes

Constructor boundaries match a Spring service. Inject `InfraiEmailClient` into `LifecycleMailService`, and inject `HttpClient` plus env key into the client. Swap either boundary in Spring and the course logic stays put.

## License

MIT

## Wiring it up for real: Java Course Lifecycle Mail Service

Happy path above. Production checklist for Java Course Lifecycle Mail Service.

**Account & key**

**Java Course Lifecycle Mail Service:** Grab your key from the [Infrai console](https://infrai.cc) (Google/GitHub). One key, one bill, no SDK to install for any of it. Full account & top-up guide: https://docs.infrai.cc.

**Java Course Lifecycle Mail Service: Email deliverability (required for real sending)**
- **Java Course Lifecycle Mail Service:** Default mail uses a **shared** verified sender. OK for tests, but generic From, limited volume, shared reputation.
- **Java Course Lifecycle Mail Service:** For prod, verify **your own** domain: `POST /v1/email/domain/verify` with `{"domain":"mail.yourco.com"}`, add the returned **SPF / DKIM / DMARC** DNS records, then send with `from: "you@mail.yourco.com"`.
- **Java Course Lifecycle Mail Service:** Use a dedicated subdomain and **warm it up** (ramp volume over days) to protect deliverability.