import { useEffect } from "react";
import { useForm } from "react-hook-form";
import { Button } from "@/components/ui/button";
import { FormInput } from "@/components/forms/FormInput";
import { Label } from "@/components/ui/label";
import type { Doctor, DoctorRequest } from "@/types";

export function DoctorForm({
  mode,
  initial,
  loading,
  onSubmit,
  onCancel,
}: {
  mode: "create" | "edit";
  initial?: Doctor | null;
  loading?: boolean;
  onSubmit: (data: DoctorRequest) => void;
  onCancel: () => void;
}) {
  const { handleSubmit, register, reset } = useForm<DoctorRequest>({
    defaultValues: {
      fullName: "",
      department: "",
      phone: "",
      email: "",
      experience: 0,
      username: "",
      password: "",
    },
  });

  useEffect(() => {
    if (!initial) {
      reset({
        fullName: "",
        department: "",
        phone: "",
        email: "",
        experience: 0,
        username: "",
        password: "",
      });
      return;
    }

    reset({
      fullName: initial.fullName ?? "",
      department: initial.department ?? "",
      phone: initial.phone ?? "",
      email: initial.email ?? "",
      experience: initial.experience ?? 0,
      username: initial.username ?? "",
      password: "",
    });
  }, [initial, reset]);

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
      <div className="grid gap-4 sm:grid-cols-2">
        <div>
          <FormInput
            id="fullName"
            label="Full Name"
            {...register("fullName", { required: true })}
          />
        </div>
        <div>
          <FormInput
            id="department"
            label="Department"
            {...register("department", { required: true })}
          />
        </div>
        <div>
          <FormInput id="phone" label="Phone" {...register("phone", { required: true })} />
        </div>
        <div>
          <FormInput id="email" label="Email" {...register("email", { required: true })} />
        </div>
        <div>
          <FormInput
            id="experience"
            label="Experience (years)"
            type="number"
            {...register("experience", { valueAsNumber: true, required: true })}
          />
        </div>
        {mode === "create" ? (
          <>
            <div>
              <FormInput id="username" label="Username" {...register("username", { required: true })} />
            </div>
            <div>
              <FormInput
                id="password"
                label="Password"
                type="password"
                {...register("password", { required: true })}
              />
            </div>
          </>
        ) : null}
      </div>

      <div className="flex items-center justify-end gap-2 pt-4">
        <Button type="button" variant="outline" onClick={onCancel} disabled={loading}>
          Cancel
        </Button>
        <Button type="submit" disabled={loading}>
          {mode === "create" ? "Create Doctor" : "Save Changes"}
        </Button>
      </div>
    </form>
  );
}
