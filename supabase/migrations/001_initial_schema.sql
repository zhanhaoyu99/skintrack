-- SkinTrack Initial Database Schema
-- Supabase PostgreSQL + RLS

-- ══════════════════════════════════════════════
-- 1. Users profile (extends Supabase Auth)
-- ══════════════════════════════════════════════
create table if not exists public.profiles (
    id uuid primary key references auth.users(id) on delete cascade,
    email text not null,
    display_name text,
    skin_type text default 'COMBINATION',
    avatar_url text,
    created_at timestamptz default now(),
    updated_at timestamptz default now()
);

alter table public.profiles enable row level security;
create policy "Users can view own profile" on public.profiles for select using (auth.uid() = id);
create policy "Users can update own profile" on public.profiles for update using (auth.uid() = id);
create policy "Users can insert own profile" on public.profiles for insert with check (auth.uid() = id);

-- ══════════════════════════════════════════════
-- 2. Skin records
-- ══════════════════════════════════════════════
create table if not exists public.skin_records (
    id uuid primary key default gen_random_uuid(),
    user_id uuid not null references public.profiles(id) on delete cascade,
    skin_type text not null default 'COMBINATION',
    overall_score int,
    acne_count int,
    pore_score int,
    redness_score int,
    even_score int,
    blackhead_density int,
    notes text,
    image_url text,
    analysis_json jsonb,
    recorded_at timestamptz not null default now(),
    created_at timestamptz not null default now()
);

create index idx_skin_records_user_date on public.skin_records(user_id, recorded_at desc);

alter table public.skin_records enable row level security;
create policy "Users can CRUD own records" on public.skin_records for all using (auth.uid() = user_id);

-- ══════════════════════════════════════════════
-- 3. Skincare products
-- ══════════════════════════════════════════════
create table if not exists public.skincare_products (
    id uuid primary key default gen_random_uuid(),
    user_id uuid not null references public.profiles(id) on delete cascade,
    name text not null,
    brand text,
    category text not null default 'MOISTURIZER',
    image_url text,
    barcode text,
    created_at timestamptz not null default now()
);

create index idx_products_user on public.skincare_products(user_id);

alter table public.skincare_products enable row level security;
create policy "Users can CRUD own products" on public.skincare_products for all using (auth.uid() = user_id);

-- ══════════════════════════════════════════════
-- 4. Daily product usage
-- ══════════════════════════════════════════════
create table if not exists public.daily_product_usage (
    id uuid primary key default gen_random_uuid(),
    user_id uuid not null references public.profiles(id) on delete cascade,
    product_id uuid not null references public.skincare_products(id) on delete cascade,
    used_date date not null,
    created_at timestamptz not null default now(),
    unique(user_id, product_id, used_date)
);

create index idx_usage_user_date on public.daily_product_usage(user_id, used_date desc);

alter table public.daily_product_usage enable row level security;
create policy "Users can CRUD own usage" on public.daily_product_usage for all using (auth.uid() = user_id);

-- ══════════════════════════════════════════════
-- 5. User subscriptions
-- ══════════════════════════════════════════════
create table if not exists public.user_subscriptions (
    user_id uuid primary key references public.profiles(id) on delete cascade,
    plan text not null default 'FREE',
    start_date timestamptz not null default now(),
    expiry_date timestamptz not null,
    is_active boolean not null default true,
    created_at timestamptz not null default now(),
    updated_at timestamptz default now()
);

alter table public.user_subscriptions enable row level security;
create policy "Users can view own subscription" on public.user_subscriptions for select using (auth.uid() = user_id);
create policy "Users can update own subscription" on public.user_subscriptions for update using (auth.uid() = user_id);
create policy "Users can insert own subscription" on public.user_subscriptions for insert with check (auth.uid() = user_id);

-- ══════════════════════════════════════════════
-- 6. Check-in streaks
-- ══════════════════════════════════════════════
create table if not exists public.check_in_streaks (
    user_id uuid primary key references public.profiles(id) on delete cascade,
    current_streak int not null default 0,
    longest_streak int not null default 0,
    last_check_in_date date,
    updated_at timestamptz default now()
);

alter table public.check_in_streaks enable row level security;
create policy "Users can CRUD own streak" on public.check_in_streaks for all using (auth.uid() = user_id);

-- ══════════════════════════════════════════════
-- 7. Storage bucket for skin photos
-- ══════════════════════════════════════════════
insert into storage.buckets (id, name, public, file_size_limit, allowed_mime_types)
values ('skin-photos', 'skin-photos', false, 5242880, array['image/jpeg', 'image/png'])
on conflict (id) do nothing;

-- Storage RLS: users can only access their own photos (path: {user_id}/*)
create policy "Users can upload own photos" on storage.objects
    for insert with check (bucket_id = 'skin-photos' and (storage.foldername(name))[1] = auth.uid()::text);
create policy "Users can view own photos" on storage.objects
    for select using (bucket_id = 'skin-photos' and (storage.foldername(name))[1] = auth.uid()::text);
create policy "Users can delete own photos" on storage.objects
    for delete using (bucket_id = 'skin-photos' and (storage.foldername(name))[1] = auth.uid()::text);

-- ══════════════════════════════════════════════
-- 8. Trigger: auto-create profile on signup
-- ══════════════════════════════════════════════
create or replace function public.handle_new_user()
returns trigger as $$
begin
    insert into public.profiles (id, email, display_name)
    values (new.id, new.email, coalesce(new.raw_user_meta_data->>'display_name', split_part(new.email, '@', 1)));

    -- Create initial subscription (14-day trial)
    insert into public.user_subscriptions (user_id, plan, start_date, expiry_date, is_active)
    values (new.id, 'FREE', now(), now() + interval '14 days', true);

    -- Create initial streak
    insert into public.check_in_streaks (user_id) values (new.id);

    return new;
end;
$$ language plpgsql security definer;

create or replace trigger on_auth_user_created
    after insert on auth.users
    for each row execute procedure public.handle_new_user();
