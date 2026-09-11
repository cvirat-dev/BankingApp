# Navigation in the Banking App

This application has two navigation levels:

- The **top navbar** selects a broad application section, such as `Konten`, `Buchungen`, or `Transaktionen`.
- The **side nav** shows the pages that belong to the currently selected section.

The important idea is that the URL and the route configuration are the source of truth. The menus do not need to know which component is currently displayed.

## What Angular provides

The navigation works because Angular Router supplies a set of routing APIs. The names `sideNav`, `section`, `sideNavItems`, and `currentSection` are application-specific; Angular does not know what those names mean.

### `RouterModule.forRoot(routes)`

`RouterModule` is Angular's router package. In `AppRoutingModule`, this line registers the application's route table:

```ts
imports: [RouterModule.forRoot(routes)]
```

`forRoot(routes)` configures one application-wide router. It tells Angular which URL patterns exist and which component should be activated for each pattern. It also enables router directives and services such as `routerLink`, `routerLinkActive`, `router-outlet`, and `Router`.

The route table is evaluated from top to bottom. The first matching route is used. The wildcard route (`**`) is a fallback, so it should normally remain near the end.

### `router-outlet`

`router-outlet` is an Angular directive that marks a location where the active route component should be inserted:

```html
<router-outlet></router-outlet>
```

It is a placeholder, not a component chosen by the application. If the URL is `/konto-bearbeiten`, Angular creates and displays `KontoEditComponent` at that location. When the URL changes, Angular removes the previous routed component and inserts the new one.

The surrounding `AppComponent` stays in place. That is why the navbar and side nav can remain visible while only the content inside `router-outlet` changes.

### `routerLink`

`routerLink` is an Angular template directive for navigating without manually changing `window.location`:

```html
<a routerLink="/konto-bearbeiten">Konto Bearbeiten</a>
```

When the user clicks the link, Angular asks the `Router` to navigate to `/konto-bearbeiten`. Angular updates the browser URL, matches the route, activates the component, and emits router events.

Using `routerLink` instead of a normal `href` keeps navigation inside the Angular application. The page is not fully reloaded for an ordinary internal route change.

### `routerLinkActive`

`routerLinkActive` is another Angular directive. It adds the named CSS class when its link matches the current router URL:

```html
<a routerLink="/konten" routerLinkActive="active">Konten Liste</a>
```

In this project it is used for side-nav links because each link represents one page. Angular can compare `/konten` with the current URL and add or remove `active` automatically.

It does not understand business sections. Angular only compares URLs. Therefore, a link to `/konten` does not automatically become active on `/konto-erstellen`, even if the application considers both routes part of the `Konten` section. That grouping is implemented with the custom `data.section` value and `currentSection` property.

### `Router`

`Router` is an injectable Angular service. `AppComponent` receives it through its constructor:

```ts
constructor(private router: Router) {}
```

The router exposes an observable `events` stream. Angular publishes router lifecycle events there, including navigation start, route recognition, guards, navigation completion, and navigation errors.

This application listens for `NavigationEnd`:

```ts
this.router.events.pipe(
  filter(event => event instanceof NavigationEnd)
)
```

`NavigationEnd` means the navigation completed successfully. It is a useful point to update menu state because the new route is now active.

### `ActivatedRoute`

`ActivatedRoute` represents the route currently active at a particular place in the router tree. It is different from `Router`:

- `Router` performs navigation and publishes navigation events.
- `ActivatedRoute` exposes information about the route that is currently active.

`AppComponent` starts at its first child route and follows child routes until it reaches the deepest route:

```ts
let r = this.route.firstChild;
while (r?.firstChild) r = r.firstChild;
```

The deepest route is used because nested Angular routes can each have their own component and metadata. In the current application the routes are mostly flat, but this traversal also works when child routes are added later.

### Route `data`

`data` is an Angular route configuration feature for static, developer-defined metadata:

```ts
data: {
  sideNav: KONTO_SIDE_NAV,
  section: 'konten'
}
```

Angular stores this object on the route snapshot. Angular does not interpret `sideNav` or `section`; the application reads those values and gives them meaning.

`r.snapshot.data` is a snapshot of the metadata at the time the route was activated. The current code reads it after `NavigationEnd`, then copies the values into component properties. For dynamic values that can change while staying on the same route, Angular also provides observable route properties such as `route.data`, `route.params`, and `route.queryParams`.

### Angular template binding

The expression below is Angular property/class binding:

```html
[class.active]="currentSection === 'konten'"
```

Angular evaluates the expression and adds the CSS class when it is `true`; it removes the class when it is `false`. The CSS rule `.nav-links a.active` then controls the visual appearance.

The `*ngFor` expression is Angular's structural directive syntax:

```html
<a *ngFor="let item of sideNavItems">{{ item.label }}</a>
```

Angular creates one anchor element for every item in the array. `{{ item.label }}` is interpolation, which places the current item's label into the rendered text.

## What happens after a click

For a click on `Konto Bearbeiten`, the sequence is:

1. `routerLink` asks Angular's `Router` to navigate to `/konto-bearbeiten`.
2. Angular matches the route in `app-routing.module.ts`.
3. Angular activates `KontoEditComponent` and places it in `router-outlet`.
4. Angular emits `NavigationEnd` through `router.events`.
5. `AppComponent` reads the active route's `snapshot.data` through `ActivatedRoute`.
6. `sideNavItems` receives `KONTO_SIDE_NAV`, so the left menu is rendered.
7. `currentSection` receives `'konten'`, so the top `Konten` link gets the `active` class.
8. Angular evaluates the template bindings and updates the DOM.

## The three pieces

### 1. Routes select components

The route configuration maps a URL to an Angular component:

```ts
{
  path: 'konto-bearbeiten',
  component: KontoEditComponent
}
```

When the browser navigates to `/konto-bearbeiten`, Angular displays `KontoEditComponent` inside the `router-outlet` in `app.component.html`.

A route can also contain additional metadata in its `data` property. This metadata does not display anything by itself; it describes the navigation context of the route:

```ts
{
  path: 'konto-bearbeiten',
  component: KontoEditComponent,
  data: {
    sideNav: KONTO_SIDE_NAV,
    section: 'konten'
  }
}
```

Here:

- `sideNav` contains the links to display in the left menu.
- `section` identifies which top-level navbar item should be active.

The routes `/konten`, `/konto-erstellen`, and `/konto-bearbeiten` all use `section: 'konten'`. Therefore, the `Konten` item remains selected while moving between those pages, even though their URLs are different.

### 2. AppComponent reads the active route

`AppComponent` listens for Angular's `NavigationEnd` event. This event is emitted after a navigation has completed.

The code starts with the root route and walks down to the deepest active route:

```ts
let r = this.route.firstChild;
while (r?.firstChild) r = r.firstChild;
```

It then reads the active route's metadata:

```ts
return r?.snapshot.data ?? {};
```

The result is used to update the menu state:

```ts
this.sideNavItems = data['sideNav'] ?? [];
this.currentSection = data['section'] ?? null;
```

This means that navigating to another route automatically changes the sidebar and the active top-level section.

### 3. The template renders the menus

The top navbar compares each link with `currentSection`:

```html
<a routerLink="/konten" [class.active]="currentSection === 'konten'">
  Konten
</a>
```

The side nav loops over the links supplied by the active route:

```html
<a *ngFor="let item of sideNavItems"
   [routerLink]="item.link"
   routerLinkActive="active">
  {{ item.label }}
</a>
```

`routerLinkActive="active"` is suitable for the side nav because each side-nav item represents one specific page. The top navbar uses `currentSection` instead because one top-level section can contain several different URLs.

## Why the side nav used to disappear

If a route does not define `data.sideNav`, the application uses an empty array:

```ts
this.sideNavItems = data['sideNav'] ?? [];
```

Angular then has no side-nav items to render. For example, this route does not provide a sidebar:

```ts
{
  path: 'transaktion',
  component: TransaktionFormularComponent,
  data: { section: 'transaktion' }
}
```

That behavior is intentional: a section can have no submenu. If it should have one, add `sideNav` to the route's `data`.

## Adding a new page to the Konten section

1. Add the component and its route.
2. Add the route to the shared `KONTO_SIDE_NAV` list.
3. Set `section: 'konten'` on the route.
4. Make sure the route's `data` includes `sideNav: KONTO_SIDE_NAV`.

Example:

```ts
const KONTO_SIDE_NAV = [
  { label: 'Konten Liste', link: '/konten' },
  { label: 'Konto Erstellen', link: '/konto-erstellen' },
  { label: 'Konto Bearbeiten', link: '/konto-bearbeiten' },
  { label: 'Konto Details', link: '/konto-details' }
];

{
  path: 'konto-details',
  component: KontoDetailsComponent,
  data: {
    sideNav: KONTO_SIDE_NAV,
    section: 'konten'
  }
}
```

The new item will then appear on every Konten-related page, and the `Konten` navbar item will remain active.

## Adding a new top-level section

For a new section, create a separate side-nav list and use a new section key:

```ts
const TRANSAKTION_SIDE_NAV = [
  { label: 'Neue Transaktion', link: '/transaktion' },
  { label: 'Transaktionsverlauf', link: '/transaktion/verlauf' }
];
```

Then add that metadata to every route belonging to the section:

```ts
{
  path: 'transaktion',
  component: TransaktionFormularComponent,
  data: {
    sideNav: TRANSAKTION_SIDE_NAV,
    section: 'transaktion'
  }
}
```

Finally, add a top navbar link whose comparison uses the same key:

```html
<a routerLink="/transaktion"
   [class.active]="currentSection === 'transaktion'">
  Transaktionen
</a>
```

The string must match in both places. For example, `'transaction'` and `'transaktion'` would be treated as two different sections.

## Quick mental model

Think of navigation as a data flow:

```text
User clicks a link
        |
        v
Angular changes the URL
        |
        v
Angular activates a route and its component
        |
        v
AppComponent reads route.data
        |
        +--> sideNavItems    -> left menu
        |
        +--> currentSection  -> active top navbar item
```

When debugging a menu, check these things in order:

1. Does the link point to the expected URL?
2. Does that URL have a route in `app-routing.module.ts`?
3. Does the route define the expected `data.sideNav` and `data.section`?
4. Does the template compare against the same section string?
5. Does the route's component appear inside the `router-outlet`?
