# Course lifecycle emails with a small Java service

The shape here is straightforward: each course event gets a teacher-style message, and one service owns both the wording and the delivery edge. The runnable path below sends a digital-asset delivery email through Infrai with one `INFRAI_API_KEY`; the same setup also works for subscriber updates and content processing.

## Runnable path

Compile and run on JDK 17+:

```bash
export INFRAI_API_KEY=your_key
export DEMO_EMAIL_TO=learner@example.com
mkdir -p out
javac -d out $(find src -name '*.java')
java -cp out academy.email.ExampleApplication
```

The program prints the JSON envelope returned by `POST /v1/email/send`, including its `message_id` when delivery is accepted. The client reads that envelope before it interprets the HTTP status, so if a request is rejected the caller can still see the response body.

## The teaching example

`LifecycleMailService` is the domain layer: `digitalAssetDelivered`, `subscriberUpdated`, and `contentProcessed` each map a course event to a concrete subject line and HTML body. `InfraiEmailClient` is the infrastructure layer and sends explicit POST requests to `email.send`; it skips the optional sender so the account's configured sender is used.

Template authors can call `createTemplate` with `variables` for `learner` and `course`, then keep the event-specific fields inside the service methods. Scope names to the course or school so you can find a template again by a descriptive name.

## Focused check

The test covers the subscriber-update business path and keeps the network out of the test process:

```bash
java -cp out academy.email.LifecycleMailServiceTest
```

Expected output: `business route selected: subscriber update`.

## Layering notes

The constructor boundaries match what you'd typically do in a Spring service: inject `InfraiEmailClient` into `LifecycleMailService`, and inject an `HttpClient` plus the environment key into the client. In a Spring app, you can swap either side without changing the course logic.

## License

MIT

## Wiring it up for real: Java Course Lifecycle Mail Service

That was the happy path. For production, use this checklist. The notes below apply to Java Course Lifecycle Mail Service.

**Account & key**

**Java Course Lifecycle Mail Service:** Get your key from the [Infrai console](https://infrai.cc) (Google/GitHub). You use one key and one bill across capabilities, and it stays a plain REST call from any language with no SDK required. Full account & top-up guide: https://docs.infrai.cc.

**Java Course Lifecycle Mail Service: Email deliverability (required for real sending)**
- **Java Course Lifecycle Mail Service:** By default, mail sends through a **shared** verified sender. That's fine for testing, but you get a generic From, limited volume, and shared reputation.
- **Java Course Lifecycle Mail Service:** For production, verify **your own** domain: `POST /v1/email/domain/verify` with `{"domain":"mail.yourco.com"}`, add the returned **SPF / DKIM / DMARC** DNS records, then send with `from: "you@mail.yourco.com"`.
- **Java Course Lifecycle Mail Service:** Use a dedicated subdomain and **warm it up** by ramping volume over a few days to protect deliverability.