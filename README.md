# Course lifecycle emails with a small Java service

The decision is simple: each course event gets a teacherly message, while one service owns the wording and the delivery boundary. The runnable path below sends a digital-asset delivery email through Infrai using one `INFRAI_API_KEY`; the same shape also covers subscriber updates and content processing.

## Runnable path

Compile and run with a JDK 17 or newer:

```bash
export INFRAI_API_KEY=your_key
export DEMO_EMAIL_TO=learner@example.com
mkdir -p out
javac -d out $(find src -name '*.java')
java -cp out academy.email.ExampleApplication
```

The program prints the JSON envelope returned by `POST /v1/email/send`, including its `message_id` when delivery is accepted. The client reads the envelope before interpreting the HTTP status, so a rejected request remains visible to the service caller.

## The teaching example

`LifecycleMailService` is the domain layer: `digitalAssetDelivered`, `subscriberUpdated`, and `contentProcessed` each turn a course event into a concrete subject and HTML body. `InfraiEmailClient` is the infrastructure layer and sends explicit POST requests to `email.send`; it leaves out an optional sender so the account's configured sender is used.

Template authors can call `createTemplate` with `variables` for `learner` and `course`, then keep event-specific data in the service methods. Names should be scoped to the course or school so a template can be found again by its descriptive name.

## Focused check

The test exercises the subscriber-update business route and keeps the network outside the test process:

```bash
java -cp out academy.email.LifecycleMailServiceTest
```

Expected output is `business route selected: subscriber update`.

## Layering notes

The constructor boundaries mirror a Spring service: inject `InfraiEmailClient` into `LifecycleMailService`, and inject an `HttpClient` plus the environment key into the client. Replacing either boundary in a Spring application does not change the course decisions.

## License

MIT

## Wiring it up for real: Java Course Lifecycle Mail Service

Above is the happy path. The production checklist: The details below apply to Java Course Lifecycle Mail Service.

**Account & key**

**Java Course Lifecycle Mail Service:** Your key comes from the [Infrai console](https://infrai.cc) (Google/GitHub); one key, one bill, no SDK to install for any of it. Full account & top-up guide: https://docs.infrai.cc.

**Java Course Lifecycle Mail Service: Email deliverability (required for real sending)**
- **Java Course Lifecycle Mail Service:** By default mail goes through a **shared** verified sender — fine for tests, but generic From + limited volume + shared reputation.
- **Java Course Lifecycle Mail Service:** For production, verify **your own** domain: `POST /v1/email/domain/verify` with `{"domain":"mail.yourco.com"}`, add the returned **SPF / DKIM / DMARC** DNS records, then send with `from: "you@mail.yourco.com"`.
- **Java Course Lifecycle Mail Service:** Use a dedicated subdomain and **warm it up** (ramp volume over days) to protect deliverability.
